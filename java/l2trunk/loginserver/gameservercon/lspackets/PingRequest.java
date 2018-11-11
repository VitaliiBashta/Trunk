package l2trunk.loginserver.gameservercon.lspackets;

import l2trunk.loginserver.gameservercon.SendablePacket;

public class PingRequest extends SendablePacket {
    @Override
    protected void writeImpl() {
        writeC(0xff);
    }
}