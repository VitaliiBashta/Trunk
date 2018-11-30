package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPPoll;

import java.util.Collections;
import java.util.List;

public class PollCommand implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = Collections.singletonList("poll");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        CCPPoll.bypass(activeChar, target.split(" "));
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }

}
