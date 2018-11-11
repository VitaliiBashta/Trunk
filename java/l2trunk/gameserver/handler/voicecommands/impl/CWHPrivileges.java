package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPCWHPrivilages;
import l2trunk.gameserver.scripts.Functions;

public class CWHPrivileges extends Functions implements IVoicedCommandHandler {
    private static final String[] _commandList = new String[]
            {
                    "clan"
            };

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        CCPCWHPrivilages.clanMain(activeChar, args);
        return false;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}