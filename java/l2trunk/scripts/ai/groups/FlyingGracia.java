package l2trunk.scripts.ai.groups;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class FlyingGracia implements IVoicedCommandHandler {
    @Override
    public List<String> getVoicedCommandList() {
        return List.of("fly");
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        return false;
    }
}