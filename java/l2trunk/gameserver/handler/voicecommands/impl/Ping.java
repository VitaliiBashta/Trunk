package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2trunk.gameserver.scripts.Functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Ping extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = List.of("ping");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        CCPSmallCommands.getPing(activeChar);
        return false;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }
}