package l2trunk.gameserver.network.serverpackets;

public class ExResponseFreeServer extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        writeEx(0x77);
        // just trigger
    }
}