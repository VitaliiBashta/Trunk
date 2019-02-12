package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.instancemanager.HellboundManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Hellbound extends Functions implements IVoicedCommandHandler {
    private static final List<String> _commandList = Collections.singletonList("hellbound");

    @Override
    public List<String> getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (command.equals("hellbound")) {
            activeChar.sendMessage("Hellbound occupation: " + HellboundManager.getHellboundLevel());
            activeChar.sendMessage("Confidence: " + HellboundManager.getConfidence());
        }
        return false;
    }
}
