package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2trunk.gameserver.scripts.Functions;

/**
 * @author Grivesky
 */
public class Ping extends Functions implements IVoicedCommandHandler {
    private static final String[] COMMANDS = {"ping"};

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        CCPSmallCommands.getPing(activeChar);
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}