package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2trunk.gameserver.scripts.Functions;

public class CombineTalismans extends Functions implements IVoicedCommandHandler {
    private static final String[] COMMANDS = new String[]
            {
                    "combine", "combinetalisman", "combinetalismans", "talismancombine", "talismanscombine", "talisman"
            };

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        CCPSmallCommands.combineTalismans(activeChar);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}