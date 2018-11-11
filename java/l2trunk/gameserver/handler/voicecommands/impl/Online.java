package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2trunk.gameserver.scripts.Functions;

public class Online extends Functions implements IVoicedCommandHandler {
    private static final String[] COMMANDS =
            {
                    "online"
            };

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        String answer = CCPSmallCommands.showOnlineCount();
        if (answer != null)
            activeChar.sendMessage(answer);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}