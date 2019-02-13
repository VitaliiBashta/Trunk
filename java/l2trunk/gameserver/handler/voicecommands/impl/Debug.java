package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;

import java.util.List;

public final class Debug implements IVoicedCommandHandler {

    @Override
    public List<String> getVoicedCommandList() {
        return List.of("debug");
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        if (!Config.ALT_DEBUG_ENABLED)
            return false;

        if (player.isDebug()) {
            player.setDebug(false);
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Disabled"));
        } else {
            player.setDebug(true);
            player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Enabled"));
        }
        return true;
    }
}
