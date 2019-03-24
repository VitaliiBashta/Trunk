package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public final class ServerInfo extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMAND_LIST = List.of("ver", "rev");

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    @Override
    public List<String> getVoicedCommandList() {
        return COMMAND_LIST;
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (command.equals("rev") || command.equals("ver")) {
            activeChar.sendMessage("Revision: Final Revision");
            activeChar.sendMessage("Build date: ");
        } else if (command.equals("date") || command.equals("time")) {
            activeChar.sendMessage(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
            return true;
        }

        return false;
    }
}
