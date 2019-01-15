package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPSmallCommands;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public class CombineTalismans extends Functions implements IVoicedCommandHandler {
    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        CCPSmallCommands.combineTalismans(activeChar);
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return List.of("combine", "combinetalisman", "combinetalismans", "talismancombine", "talismanscombine", "talisman");
    }
}