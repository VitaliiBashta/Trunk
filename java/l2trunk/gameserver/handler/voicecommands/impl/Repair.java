package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPRepair;
import l2trunk.gameserver.scripts.Functions;

import java.util.Collections;
import java.util.List;

public final class Repair extends Functions implements IVoicedCommandHandler {
    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        CCPRepair.repairChar(activeChar, target);
        return false;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return Collections.singletonList("repair");
    }
}