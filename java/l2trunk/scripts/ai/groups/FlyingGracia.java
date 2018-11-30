package l2trunk.scripts.ai.groups;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class FlyingGracia implements IVoicedCommandHandler, ScriptFile {
    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public List<String> getVoicedCommandList() {
        return Collections.emptyList();
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        return false;
    }
}