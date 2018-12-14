package l2trunk.gameserver.handler.admincommands;

import l2trunk.gameserver.handler.admincommands.impl.*;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        registerAdminCommandHandler(new AdminPSPoints());
        registerAdminCommandHandler(new AdminQuests());
        registerAdminCommandHandler(new AdminReload());
        registerAdminCommandHandler(new AdminRepairChar());
        registerAdminCommandHandler(new AdminRes());
        registerAdminCommandHandler(new AdminRide());
        registerAdminCommandHandler(new AdminServer());
        registerAdminCommandHandler(new AdminShop());
        registerAdminCommandHandler(new AdminShutdown());
        registerAdminCommandHandler(new AdminSkill());
        registerAdminCommandHandler(new AdminScripts());
        registerAdminCommandHandler(new AdminSpawn());
        registerAdminCommandHandler(new AdminSS());
        registerAdminCommandHandler(new AdminTarget());
        registerAdminCommandHandler(new AdminTeleport());
        registerAdminCommandHandler(new AdminTeam());
        registerAdminCommandHandler(new AdminZone());
        registerAdminCommandHandler(new AdminKill());
        registerAdminCommandHandler(new AdminMail());
        registerAdminCommandHandler(new AdminMasterwork());

        // Ady
        registerAdminCommandHandler(new AdminAugmentation());
        registerAdminCommandHandler(new AdminGmEvent());
    }

    public void registerAdminCommandHandler(IAdminCommandHandler handler) {
        for (Enum<?> e : handler.getAdminCommandEnum())
            datatable.put(e.toString().toLowerCase(), handler);
    }

    public IAdminCommandHandler getAdminCommandHandler(String adminCommand) {
        String command = adminCommand;
        if (adminCommand.contains(" "))
            command = adminCommand.substring(0, adminCommand.indexOf(" "));
        return datatable.get(command);
    }

    public void useAdminCommandHandler(Player activeChar, String adminCommand) {
        if (!(activeChar.isGM() || activeChar.getPlayerAccess().CanUseGMCommand)) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.SendBypassBuildCmd.NoCommandOrAccess", activeChar).addString(adminCommand));
            return;
        }

        String[] wordList = adminCommand.split(" ");
        IAdminCommandHandler handler = datatable.get(wordList[0]);
        if (handler != null) {
            boolean success = false;
            try {
                for (Enum<?> e : handler.getAdminCommandEnum())
                    if (e.toString().equalsIgnoreCase(wordList[0])) {
                        success = handler.useAdminCommand(e, wordList, adminCommand, activeChar);
                        break;
                    }
            } catch (RuntimeException e) {
                LOG.error("Error while using Admin Command! ", e);
            }

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

    public Set<String> getAllCommands() {
        return datatable.keySet();
    }
}