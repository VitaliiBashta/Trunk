package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.OlympiadNobleDAO;
import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.BuyListHolder;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.data.xml.holder.ProductHolder;
import l2trunk.gameserver.data.xml.parser.EventParser;
import l2trunk.gameserver.data.xml.parser.NpcParser;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.SpawnManager;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.ChangeLogManager;
import l2trunk.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.tables.FishTable;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class AdminReload implements IAdminCommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AdminReload.class);

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanReload)
            return false;

        switch (comm) {
            case "admin_reload":
                break;
            case "admin_reload_config": {
                try {
                    Config.load();
                } catch (Exception e) {
                    activeChar.sendMessage("Error: " + e.getMessage() + "!");
                    return false;
                }
                activeChar.sendMessage("Config reloaded!");
                break;
            }
            case "admin_reload_multisell": {
                try {
                    MultiSellHolder.INSTANCE.reload();
                } catch (Exception e) {
                    return false;
                }
                activeChar.sendMessage("Multisell list reloaded!");
                break;
            }
            case "admin_reload_gmaccess": {
                try {
                    Config.loadGMAccess();
                    GameObjectsStorage.getAllPlayersStream().forEach(player -> {
                        if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS)
                            player.setPlayerAccess(Config.gmlist.get(player.objectId()));
                        else
                            player.setPlayerAccess(Config.gmlist.get(0));
                    });
                } catch (RuntimeException e) {
                    return false;
                }
                activeChar.sendMessage("GMAccess reloaded!");
                break;
            }
            case "admin_reload_htm": {
                HtmCache.INSTANCE.clear();
                if (Config.HTM_CACHE_MODE == 2) {
                    HtmCache.INSTANCE.reload();
                }
                activeChar.sendMessage("HtmCache reloaded!");
                break;
            }
            case "admin_reload_qs": {
                if (fullString.endsWith("all"))
                    GameObjectsStorage.getAllPlayersStream().forEach(this::reloadQuestStates);
                else {
                    GameObject t = activeChar.getTarget();

                    if (t instanceof Player) {
                        Player p = (Player) t;
                        reloadQuestStates(p);
                    } else
                        reloadQuestStates(activeChar);
                }
                break;
            }
            case "admin_reload_qs_help": {
                activeChar.sendMessage("");
                activeChar.sendMessage("Quest Help:");
                activeChar.sendMessage("reload_qs_help - This Message.");
                activeChar.sendMessage("reload_qs <selected target> - reload all quest states for target.");
                activeChar.sendMessage("reload_qs <no target or target is not getPlayer> - reload quests for getPlayer.");
                activeChar.sendMessage("reload_qs all - reload quests for all players in world.");
                activeChar.sendMessage("");
                break;
            }
            case "admin_reload_skills": {
                ThreadPoolManager.INSTANCE.execute(SkillTable.INSTANCE::reload);
                activeChar.sendMessage("Skills Reloaded!");
                break;
            }
            case "admin_reload_npc": {
                NpcParser.INSTANCE.reload();
                break;
            }
            case "admin_reload_spawn": {
                ThreadPoolManager.INSTANCE.execute(SpawnManager.INSTANCE::reloadAll);
                break;
            }
            case "admin_reload_fish": {
                FishTable.INSTANCE.reload();
                break;
            }
            case "admin_reload_translit": {
                Strings.reload();
                break;
            }
            case "admin_reload_shops": {
                BuyListHolder.INSTANCE.reload();
                break;
            }
            case "admin_reload_static": {
                //StaticObjectsTable.INSTANCE().reloadStaticObjects();
                break;
            }
            case "admin_reload_pets": {
                PetDataTable.INSTANCE.reload();
                break;
            }
            case "admin_reload_locale": {
                StringHolder.INSTANCE.reload();
                break;
            }
            case "admin_reload_nobles": {
                OlympiadNobleDAO.select();
                OlympiadDatabase.loadNoblesRank();
                break;
            }
            case "admin_reload_im": {
                ProductHolder.getInstance().reload();
                break;
            }
            case "admin_reload_events": {
                EventHolder.clear();
                EventParser.INSTANCE.load();
                activeChar.sendMessage("Events Reloaded!");
                break;
            }
            case "admin_reload_changelog": {
                ChangeLogManager.INSTANCE.reloadChangeLog();
                activeChar.sendMessage("Changelog reloaded!");
                break;
            }
            case "admin_reload_damageclasses": {
                activeChar.sendMessage("Balance properties data have been reloaded.");
                break;
            }
        }
        activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/reload.htm"));
        return true;
    }

    private void reloadQuestStates(Player p) {
        p.getAllQuestsStates().forEach(qs ->
                p.removeQuestState(qs.quest.name));
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            Quest.restoreQuestStates(p, con);
        } catch (SQLException e) {
            LOG.error("Error while reloading Quest States ", e);
        }
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_reload",
                "admin_reload_config",
                "admin_reload_multisell",
                "admin_reload_gmaccess",
                "admin_reload_htm",
                "admin_reload_qs",
                "admin_reload_qs_help",
                "admin_reload_skills",
                "admin_reload_npc",
                "admin_reload_spawn",
                "admin_reload_fish",
                "admin_reload_translit",
                "admin_reload_shops",
                "admin_reload_static",
                "admin_reload_pets",
                "admin_reload_locale",
                "admin_reload_nobles",
                "admin_reload_im",
                "admin_reload_events",
                "admin_reload_changelog",
                "admin_reload_damageclasses");
    }
}