package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;

/**
 * This class handles following admin commands: - gm = turns gm mode on/off
 */
public class AdminGm implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditChar)
            return false;

        switch (command) {
            case admin_gm_set:
                handleGm(activeChar);
                break;
        }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void handleGm(Player activeChar) {
        if (activeChar.isGM()) {
            activeChar.getPlayerAccess().IsGM = false;
            activeChar.sendMessage("You no longer have GM status.");
        } else {
            activeChar.getPlayerAccess().IsGM = true;
            activeChar.sendMessage("You have GM status now.");
        }
    }

    private enum Commands {
        admin_gm_set
    }
}