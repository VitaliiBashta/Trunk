package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPRepair;
import l2trunk.gameserver.scripts.Functions;

public class Repair extends Functions implements IVoicedCommandHandler {
    private static final String[] COMMANDS =
            {
                    "repair"
            };

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        CCPRepair.repairChar(activeChar, target);
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return COMMANDS;
    }
}