package l2trunk.gameserver.network.loginservercon.gspackets;

import l2trunk.gameserver.network.loginservercon.SendablePacket;

public class PingResponse extends SendablePacket {
    protected void writeImpl() {
        writeC(0xff);
        writeQ(System.currentTimeMillis());
    }
}