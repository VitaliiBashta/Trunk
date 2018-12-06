package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;


public final class NetPing extends L2GameClientPacket {
    private int ping;

    @Override
    protected void readImpl() {
        int playerId = readD();
        ping = readD();
        int mtu = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        activeChar.setPing(ping);
    }

    @Override
    public String getType() {
        return "[C] B1 NetPing";
    }
}