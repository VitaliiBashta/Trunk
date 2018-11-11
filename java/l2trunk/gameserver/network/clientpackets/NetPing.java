package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;

//import l2trunk.gameserver.network.l2.Pinger;

public class NetPing extends L2GameClientPacket {
    private int playerId;
    private int ping;
    private int mtu;

    @Override
    protected void readImpl() {
        playerId = readD();
        ping = readD();
        mtu = readD();
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