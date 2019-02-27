package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.tables.GmListTable;

import java.util.List;

public final class AdminGmChat implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        if (!activeChar.getPlayerAccess().CanAnnounce)
            return false;

        switch (comm) {
            case "admin_gmchat":
                try {
                    String text = fullString.replaceFirst("admin_gmchat", "");
                    Say2 cs = new Say2(0, ChatType.ALLIANCE, activeChar.getName(), text);
                    GmListTable.broadcastToGMs(cs);
                } catch (StringIndexOutOfBoundsException e) {
                }
                break;
            case "admin_snoop": {
                GameObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendMessage("You must getBonuses a target.");
                    return false;
                }
                if (target instanceof Player) {
                    Player player = (Player) target;
                    player.addSnooper(activeChar);
                    activeChar.addSnooped(player);
                    break;
                } else {
                    activeChar.sendMessage("Target must be a getPlayer.");
                    return false;
                }
            }
            case "admin_unsnoop": {
                GameObject target = activeChar.getTarget();
                if (target == null) {
                    activeChar.sendMessage("You must getBonuses a target.");
                    return false;
                }
                if (target instanceof Player) {
                    Player player = (Player) target;
                    activeChar.removeSnooped(player);
                    activeChar.sendMessage("stoped snooping getPlayer: " + target.getName());
                    break;
                } else {
                    activeChar.sendMessage("Target must be a getPlayer.");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_gmchat",
                "admin_snoop",
                "admin_unsnoop");
    }
}