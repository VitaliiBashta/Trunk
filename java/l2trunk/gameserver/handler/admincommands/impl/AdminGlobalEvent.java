package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class AdminGlobalEvent implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        GameObject object = activeChar.getTarget();
        if (object == null)
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        else object.getEvents().forEach(e ->
                activeChar.sendMessage("- " + e.toString()));
        return false;
    }

    @Override
    public String getAdminCommand() {
        return "admin_list_events";
    }
}
