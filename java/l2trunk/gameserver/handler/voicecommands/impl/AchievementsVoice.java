package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.scripts.Functions;

public class AchievementsVoice extends Functions implements IVoicedCommandHandler {
    private static final String[] COMMANDS = new String[]
            {
                    "achievements",
                    "ach"
            };

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        Achievements.getInstance().onBypass(activeChar, "_bbs_achievements", null);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}