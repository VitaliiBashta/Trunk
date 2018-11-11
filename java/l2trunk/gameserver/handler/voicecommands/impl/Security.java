package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.loginservercon.gspackets.ChangeAllowedIp;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

public final class Security implements IVoicedCommandHandler {

    private static final String[] _commandList = {};

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {

        if (command.equalsIgnoreCase("lock")) {
            NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
            html.setFile("command/lock/lock.htm");
            html.replace("%ip_block%", IpBlockStatus());
            html.replace("%hwid_block%", HwidBlockStatus());
            html.replace("%hwid_val%", "CPU");
            html.replace("%curIP%", activeChar.getIP());
            activeChar.sendPacket(html);
            return true;
        } else if (command.equalsIgnoreCase("lockIp")) {
            return true;
        } else if (command.equalsIgnoreCase("lockHwid")) {
            return true;
        } else if (command.equalsIgnoreCase("unlockIp")) {

            AuthServerCommunication.getInstance().sendPacket(new ChangeAllowedIp(activeChar.getAccountName(), ""));

            NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
            html.setFile("command/lock/unlock_ip.htm");
            html.replace("%curIP", activeChar.getIP());
            activeChar.sendPacket(html);
            return true;
        } else if (command.equalsIgnoreCase("unlockHwid")) {


            return true;
        }

        return true;
    }

    private String IpBlockStatus() {
        return "Prohibited";
    }

    private String HwidBlockStatus() {
        return "Prohibited";
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}