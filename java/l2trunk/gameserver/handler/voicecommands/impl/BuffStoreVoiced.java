package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.scripts.Functions;

import java.util.Collections;
import java.util.List;

public final class BuffStoreVoiced extends Functions implements IVoicedCommandHandler {
    private static final List<String> VOICED_COMMANDS = List.of("buffstore");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String params) {
        try {
            // Check if the player can set a store
            if (!Config.BUFF_STORE_ALLOWED_CLASS_LIST.contains(activeChar.getClassId().getId())) {
                activeChar.sendMessage("Your profession is not allowed to set an Buff Store");
                return false;
            }

            // Shows the initial buff store window
            final NpcHtmlMessage html = new NpcHtmlMessage(0);
            html.setFile("command/buffstore/buff_store.htm");
            if (activeChar.getPrivateStoreType() == Player.STORE_PRIVATE_BUFF) {
                html.replace("%link%", "Stop Store");
                html.replace("%bypass%", "bypass -h BuffStore stopstore");
            } else {
                html.replace("%link%", "Create Store");
                html.replace("%bypass%", "bypass -h player_help command/buffstore/buff_store_create.htm");
            }
            activeChar.sendPacket(html);

            return true;
        } catch (Exception e) {
            activeChar.sendMessage("Use: .buffstore");
        }

        return false;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return VOICED_COMMANDS;
    }
}
