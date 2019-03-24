package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.itemLogs.CCPItemLogs;

import java.util.Collections;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class ItemLogsCommand implements IVoicedCommandHandler {

    @Override
    public List<String> getVoicedCommandList() {
        return List.of("itemlogs");
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        CCPItemLogs.showPage(player, toInt(args));
        return true;
    }
}