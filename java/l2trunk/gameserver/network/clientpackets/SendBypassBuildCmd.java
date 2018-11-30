package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.model.Player;

public class SendBypassBuildCmd extends L2GameClientPacket {
    private String _command;

    @Override
    protected void readImpl() {
        _command = readS();

        if (_command != null)
            _command = _command.trim();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();

        if (activeChar == null || activeChar.isBlocked())
            return;

        String cmd = _command;

        if (!cmd.contains("admin_"))
            cmd = "admin_" + cmd;

        AdminCommandHandler.INSTANCE.useAdminCommandHandler(activeChar, cmd);
    }
}