package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;

public final class AdminTarget implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        if (!activeChar.getPlayerAccess().CanViewChar)
            return false;

        try {
            String targetName = wordList[1];
            GameObject obj = World.getPlayer(targetName);
            if (obj != null)
                obj.onAction(activeChar, false);
            else
                activeChar.sendMessage("Player " + targetName + " not found");
        } catch (IndexOutOfBoundsException e) {
            activeChar.sendMessage("Please specify correct name.");
        }
        return true;
    }

    @Override
    public String getAdminCommand() {
        return "admin_target";
    }
}