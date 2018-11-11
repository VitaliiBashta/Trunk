package l2trunk.loginserver.gameservercon.lspackets;

import l2trunk.loginserver.gameservercon.SendablePacket;

public class AskForIds extends SendablePacket {
    private final int _count;

    public AskForIds(int count) {
        _count = count;
    }

    @Override
    protected void writeImpl() {
        writeC(0x07);
        writeD(_count);
    }
}
