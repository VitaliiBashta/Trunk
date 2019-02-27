package l2trunk.gameserver.handler.admincommands;

import l2trunk.gameserver.handler.admincommands.impl.*;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.utils.Log;
import l2trunk.scripts.handler.admincommands.AdminBosses;
import l2trunk.scripts.handler.admincommands.AdminEpic;
import l2trunk.scripts.handler.admincommands.AdminResidence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum AdminCommandHandler {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(AdminCommandHandler.class);
    private final Map<String, IAdminCommandHandler> datatable = new HashMap<>();

    AdminCommandHandler() {
        registerAdminCommandHandler(new AdminAdmin());
        registerAdminCommandHandler(new AdminAnnouncements());
        registerAdminCommandHandler(new AdminAttribute());
        registerAdminCommandHandler(new AdminCamera());
        registerAdminCommandHandler(new AdminCancel());
        registerAdminCommandHandler(new AdminClanHall());
        registerAdminCommandHandler(new AdminPoll());
        registerAdminCommandHandler(new AdminClientSupport());
        registerAdminCommandHandler(new AdminCreateItem());
        registerAdminCommandHandler(new AdminCursedWeapons());
        registerAdminCommandHandler(new AdminDelete());
        registerAdminCommandHandler(new AdminDisconnect());
        registerAdminCommandHandler(new AdminDoorControl());
        registerAdminCommandHandler(new AdminEditChar());
        registerAdminCommandHandler(new AdminEffects());
        registerAdminCommandHandler(new AdminEnchant());
        registerAdminCommandHandler(new AdminEvents());
        registerAdminCommandHandler(new AdminGeodata());
        registerAdminCommandHandler(new AdminGiveAll());
        registerAdminCommandHandler(new AdminGlobalEvent());
        registerAdminCommandHandler(new AdminGm());
        registerAdminCommandHandler(new AdminGmChat());
        registerAdminCommandHandler(new AdminHeal());
        registerAdminCommandHandler(new AdminHellbound());
        registerAdminCommandHandler(new AdminHelpPage());
        registerAdminCommandHandler(new AdminInstance());
        registerAdminCommandHandler(new AdminIP());
        registerAdminCommandHandler(new AdminLevel());
        registerAdminCommandHandler(new AdminMammon());
        registerAdminCommandHandler(new AdminManor());
        registerAdminCommandHandler(new AdminMenu());
        registerAdminCommandHandler(new AdminPanel());
        registerAdminCommandHandler(new AdminNochannel());
        registerAdminCommandHandler(new AdminOlympiad());
        registerAdminCommandHandler(new AdminPetition());
        registerAdminCommandHandler(new AdminPledge());
        registerAdminCommandHandler(new AdminPolymorph());
        registerAdminCommandHandler(new AdminQuests());
        registerAdminCommandHandler(new AdminReload());
        registerAdminCommandHandler(new AdminRepairChar());
        registerAdminCommandHandler(new AdminRes());
        registerAdminCommandHandler(new AdminRide());
        registerAdminCommandHandler(new AdminServer());
        registerAdminCommandHandler(new AdminShop());
        registerAdminCommandHandler(new AdminShutdown());
        registerAdminCommandHandler(new AdminSkill());
        registerAdminCommandHandler(new AdminSpawn());
        registerAdminCommandHandler(new AdminSS());
        registerAdminCommandHandler(new AdminTarget());
        registerAdminCommandHandler(new AdminTeleport());
        registerAdminCommandHandler(new AdminTeam());
        registerAdminCommandHandler(new AdminZone());
        registerAdminCommandHandler(new AdminKill());
        registerAdminCommandHandler(new AdminMail());
        registerAdminCommandHandler(new AdminMasterwork());
        registerAdminCommandHandler(new AdminBosses());
        registerAdminCommandHandler(new AdminResidence());
        registerAdminCommandHandler(new AdminEpic());


        // Ady
        registerAdminCommandHandler(new AdminAugmentation());
        registerAdminCommandHandler(new AdminGmEvent());
    }

    public void registerAdminCommandHandler(IAdminCommandHandler handler) {
        handler.getAdminCommands().forEach(c -> datatable.put(c.toLowerCase(), handler));
    }

    public void useAdminCommandHandler(Player activeChar, String adminCommand) {
        if (!(activeChar.isGM() || activeChar.getPlayerAccess().CanUseGMCommand)) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess").addString(adminCommand));
            return;
        }

        String[] wordList = adminCommand.split(" ");
        IAdminCommandHandler handler = datatable.get(wordList[0]);
        if (handler != null) {
            boolean success = handler.getAdminCommands().stream()
                    .filter(e -> e.equalsIgnoreCase(wordList[0]))
                    .findFirst()
                    .map(e -> handler.useAdminCommand(e, wordList, adminCommand, activeChar))
                    .orElse(false);

            Log.LogCommand(activeChar, activeChar.getTarget(), adminCommand, success);
        }
    }

    public void log() {
        LOG.info(String.format("loaded %d %s(s) count.", size(), getClass().getSimpleName()));
    }

    public int size() {
        return datatable.size();
    }

    public void clear() {
        datatable.clear();
    }

}