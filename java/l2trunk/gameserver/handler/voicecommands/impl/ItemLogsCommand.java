package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.itemLogs.CCPItemLogs;
import l2trunk.gameserver.utils.Util;

public class ItemLogsCommand implements IVoicedCommandHandler {
    private static final String[] _commandList =
            {
                    "itemlogs"
            };

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        CCPItemLogs.showPage(player, Util.getInteger(args, 0));
        return true;
    }
}