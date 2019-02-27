package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;

public final class AdminGm implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditChar)
            return false;

        if (activeChar.isGM()) {
            activeChar.getPlayerAccess().IsGM = false;
            activeChar.sendMessage("You no longer have GM status.");
        } else {
            activeChar.getPlayerAccess().IsGM = true;
            activeChar.sendMessage("You have GM status now.");
        }

        return true;
    }

    @Override
    public String getAdminCommand() {
        return "admin_gm_set";
    }
}