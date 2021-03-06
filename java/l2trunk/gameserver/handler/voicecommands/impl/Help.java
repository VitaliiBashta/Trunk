package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.network.serverpackets.RadarControl;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class Help extends Functions implements IVoicedCommandHandler {
    private static final List<String> _commandList = List.of("changes", "whereis", "exp");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        command = command.intern();
        if (command.equalsIgnoreCase("changes"))
            return help(command, activeChar, args);
        if (command.equalsIgnoreCase("whereis"))
            return whereis(command, activeChar, args);
        if (command.equalsIgnoreCase("exp"))
            return exp(command, activeChar, args);

        return false;
    }

    private boolean exp(String command, Player activeChar, String args) {
        if (activeChar.getLevel() >= (activeChar.isSubClassActive() ? Experience.getMaxSubLevel() : Experience.getMaxLevel()))
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Help.MaxLevel"));
        else {
            long exp = Experience.LEVEL[activeChar.getLevel() + 1] - activeChar.getExp();
            activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Help.ExpLeft").addNumber(exp));
        }
        return true;
    }

    private boolean whereis(String command, Player activeChar, String args) {
        Player friend = World.getPlayer(args);
        if (friend == null)
            return false;

        if (friend.getParty() == activeChar.getParty() || friend.getClan() == activeChar.getClan()) {
            RadarControl rc = new RadarControl(0, 1, friend.getLoc());
            activeChar.sendPacket(rc);
            return true;
        }

        return false;
    }

    private boolean help(String command, Player activeChar, String args) {
        String dialog = HtmCache.INSTANCE.getNotNull("command/help.htm", activeChar);
        show(dialog, activeChar);
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return _commandList;
    }
}