package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.tables.GmListTable;

public class AdminGmChat implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanAnnounce)
            return false;

        switch (command) {
            case admin_gmchat:
                try {
                    String text = fullString.replaceFirst(Commands.admin_gmchat.name(), "");
                    Say2 cs = new Say2(0, ChatType.ALLIANCE, activeChar.getName(), text);
                    GmListTable.broadcastToGMs(cs);
                } catch (StringIndexOutOfBoundsException e) {
                }
                break;
            case admin_snoop: {
                GameObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendMessage("You must getBonuses a target.");
                    return false;
                }
                if (!target.isPlayer()) {
                    activeChar.sendMessage("Target must be a player.");
                    return false;
                }
                Player player = (Player) target;
                player.addSnooper(activeChar);
                activeChar.addSnooped(player);
                break;
            }
            case admin_unsnoop: {
                GameObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendMessage("You must getBonuses a target.");
                    return false;
                }
                if (!target.isPlayer()) {
                    activeChar.sendMessage("Target must be a player.");
                    return false;
                }
                Player player = (Player) target;
                activeChar.removeSnooped(player);
                activeChar.sendMessage("stoped snooping player: " + target.getName());
                break;
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_gmchat,
        admin_snoop,
        admin_unsnoop
    }
}