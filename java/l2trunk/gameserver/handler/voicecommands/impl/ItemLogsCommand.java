package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.itemLogs.CCPItemLogs;
import l2trunk.gameserver.utils.Util;

import java.util.Collections;
import java.util.List;

public class ItemLogsCommand implements IVoicedCommandHandler {

    @Override
    public List<String> getVoicedCommandList() {
        return Collections.singletonList("itemlogs");
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        CCPItemLogs.showPage(player, Util.getInteger(args, 0));
        return true;
    }
}