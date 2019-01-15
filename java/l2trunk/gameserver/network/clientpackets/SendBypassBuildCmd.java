package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.model.Player;

public final class SendBypassBuildCmd extends L2GameClientPacket {
    private String command;

    @Override
    protected void readImpl() {
        command = readS();

        if (command != null)
            command = command.trim();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();

        if (activeChar == null || activeChar.isBlocked())
            return;

        String cmd = command;

        if (!cmd.contains("admin_"))
            cmd = "admin_" + cmd;

        AdminCommandHandler.INSTANCE.useAdminCommandHandler(activeChar, cmd);
    }
}