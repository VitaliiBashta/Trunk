package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.BalancerConfig;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.OlympiadNobleDAO;
import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.*;
import l2trunk.gameserver.data.xml.parser.ClassesStatsBalancerParser;
import l2trunk.gameserver.data.xml.parser.EventParser;
import l2trunk.gameserver.data.xml.parser.FightClubMapParser;
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
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.tables.FishTable;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminReload implements IAdminCommandHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AdminReload.class);

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanReload)
            return false;

        switch (command) {
            case admin_reload:
                break;
            case admin_reload_config: {
                try {
                    Config.load();
                } catch (Exception e) {
                    activeChar.sendMessage("Error: " + e.getMessage() + "!");
                    return false;
                }
                activeChar.sendMessage("Config reloaded!");
                break;
            }
            case admin_reload_multisell: {
                try {
                    MultiSellHolder.getInstance().reload();
                } catch (Exception e) {
                    return false;
                }
                activeChar.sendMessage("Multisell list reloaded!");
                break;
            }
            case admin_reload_gmaccess: {
                try {
                    Config.loadGMAccess();
                    GameObjectsStorage.getAllPlayers().forEach(player -> {
                        if (!Config.EVERYBODY_HAS_ADMIN_RIGHTS)
                            player.setPlayerAccess(Config.gmlist.get(player.getObjectId()));
                        else
                            player.setPlayerAccess(Config.gmlist.get(0));
                    });
                } catch (RuntimeException e) {
                    return false;
                }
                activeChar.sendMessage("GMAccess reloaded!");
                break;
            }
            case admin_reload_htm: {
                HtmCache.INSTANCE.clear();
                if (Config.HTM_CACHE_MODE == 2) {
                    HtmCache.INSTANCE.reload();
                }
                activeChar.sendMessage("HtmCache reloaded!");
                break;
            }
            case admin_reload_qs: {
                if (fullString.endsWith("all"))
                    GameObjectsStorage.getAllPlayers().forEach(this::reloadQuestStates);
                else {
                    GameObject t = activeChar.getTarget();

                    if (t != null && t.isPlayer()) {
                        Player p = (Player) t;
                        reloadQuestStates(p);
                    } else
                        reloadQuestStates(activeChar);
                }
                break;
            }
            case admin_reload_qs_help: {
                activeChar.sendMessage("");
                activeChar.sendMessage("Quest Help:");
                activeChar.sendMessage("reload_qs_help - This Message.");
                activeChar.sendMessage("reload_qs <selected target> - reload all quest states for target.");
                activeChar.sendMessage("reload_qs <no target or target is not player> - reload quests for self.");
                activeChar.sendMessage("reload_qs all - reload quests for all players in world.");
                activeChar.sendMessage("");
                break;
            }
            case admin_reload_skills: {
                ThreadPoolManager.INSTANCE.execute(() -> SkillTable.INSTANCE().reload());
                activeChar.sendMessage("Skills Reloaded!");
                break;
            }
            case admin_reload_npc: {
                NpcParser.getInstance().reload();
                break;
            }
            case admin_reload_spawn: {
                ThreadPoolManager.INSTANCE.execute(SpawnManager.INSTANCE::reloadAll);
                break;
            }
            case admin_reload_fish: {
                FishTable.INSTANCE.reload();
                break;
            }
            case admin_reload_translit: {
                Strings.reload();
                break;
            }
            case admin_reload_shops: {
                BuyListHolder.INSTANCE.reload();
                break;
            }
            case admin_reload_static: {
                //StaticObjectsTable.INSTANCE().reloadStaticObjects();
                break;
            }
            case admin_reload_pets: {
                PetDataTable.getInstance().reload();
                break;
            }
            case admin_reload_locale: {
                StringHolder.INSTANCE.reload();
                break;
            }
            case admin_reload_nobles: {
                OlympiadNobleDAO.select();
                OlympiadDatabase.loadNoblesRank();
                break;
            }
            case admin_reload_im: {
                ProductHolder.getInstance().reload();
                break;
            }
            case admin_reload_events: {
                EventHolder.getInstance().clear();
                EventParser.getInstance().load();
                activeChar.sendMessage("Events Reloaded!");
                break;
            }
            case admin_reload_fc_maps: {
                FightClubMapHolder.getInstance().clear();
                FightClubMapParser.getInstance().load();
                activeChar.sendMessage("Maps Reloaded!");
                break;
            }
            case admin_reload_changelog: {
                ChangeLogManager.INSTANCE.reloadChangeLog();
                activeChar.sendMessage("Changelog reloaded!");
                break;
            }
            case admin_reload_balanceclasses: {
                ClassesStatsBalancerParser.getInstance().reload();
                activeChar.sendMessage("Balance Classes reloaded!");
                break;
            }
            case admin_reload_damageclasses: {
                BalancerConfig.LoadConfig();
                activeChar.sendMessage("Balance properties data have been reloaded.");
                break;
            }
        }
        activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/reload.htm"));
        return true;
    }

    private void reloadQuestStates(Player p) {
        for (QuestState qs : p.getAllQuestsStates())
            p.removeQuestState(qs.getQuest().getName());
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            Quest.restoreQuestStates(p, con);
        } catch (SQLException e) {
            LOG.error("Error while reloading Quest States ", e);
        }
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_reload,
        admin_reload_config,
        admin_reload_multisell,
        admin_reload_gmaccess,
        admin_reload_htm,
        admin_reload_qs,
        admin_reload_qs_help,
        admin_reload_skills,
        admin_reload_npc,
        admin_reload_spawn,
        admin_reload_fish,
        admin_reload_translit,
        admin_reload_shops,
        admin_reload_static,
        admin_reload_pets,
        admin_reload_locale,
        admin_reload_nobles,
        admin_reload_im,
        admin_reload_events,
        admin_reload_fc_maps,
        admin_reload_changelog,
        admin_reload_balanceclasses,
        admin_reload_damageclasses
    }
}