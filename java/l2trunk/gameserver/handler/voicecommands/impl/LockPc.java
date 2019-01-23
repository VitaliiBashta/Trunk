package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CharacterControlPanel;
import l2trunk.gameserver.scripts.Functions;

import java.util.Collections;
import java.util.List;

public class LockPc extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = Collections.singletonList("lock");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        String nextPage = CharacterControlPanel.INSTANCE.useCommand(activeChar, "hwidPage", "-h user_control ");

        if (nextPage == null || nextPage.isEmpty())
            return true;
        String html = "command/" + nextPage;

        String dialog = HtmCache.INSTANCE.getNotNull(html, activeChar);

        dialog = CharacterControlPanel.INSTANCE.replacePage(dialog, activeChar, "", "-h user_control ");

        show(dialog, activeChar);

        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }

}
