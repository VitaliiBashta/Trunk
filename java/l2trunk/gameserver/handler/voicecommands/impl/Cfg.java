package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CharacterControlPanel;
import l2trunk.gameserver.scripts.Functions;

import java.util.Arrays;
import java.util.List;

public final class Cfg extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = List.of("control", "cfg", "menu");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        String nextPage = CharacterControlPanel.getInstance().useCommand(activeChar, args, "-h user_control ");

        if (nextPage == null || nextPage.isEmpty())
            return true;

        String html = "command/" + nextPage;

        String dialog = HtmCache.INSTANCE.getNotNull(html, activeChar);

        String additionalText = args.split(" ").length > 1 ? args.split(" ")[1] : "";
        dialog = CharacterControlPanel.getInstance().replacePage(dialog, activeChar, additionalText, "-h user_control ");

        show(dialog, activeChar);

        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }
}