package l2trunk.gameserver.handler.usercommands.impl;

import l2trunk.gameserver.handler.usercommands.IUserCommandHandler;
import l2trunk.gameserver.model.Player;

import java.util.Collections;
import java.util.List;

/**
 * Support for /resetname command
 */
public final class ResetName implements IUserCommandHandler {
    private static final int COMMAND_ID = 117;

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_ID != id)
            return false;

        if (activeChar.getVar("oldtitle") != null) {
            activeChar.setTitleColor(Player.DEFAULT_TITLE_COLOR);
            activeChar.setTitle(activeChar.getVar("oldtitle"));
            activeChar.broadcastUserInfo(true);
            return true;
        }
        return false;
    }

    @Override
    public final List<Integer> getUserCommandList() {
        return List.of(COMMAND_ID);
    }
}
