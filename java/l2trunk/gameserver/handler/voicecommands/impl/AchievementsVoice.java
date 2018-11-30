package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.scripts.Functions;

import java.util.Arrays;
import java.util.List;

public final class AchievementsVoice extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = Arrays.asList("achievements", "ach");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        Achievements.INSTANCE.onBypass(activeChar, "_bbs_achievements", null);
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }
}