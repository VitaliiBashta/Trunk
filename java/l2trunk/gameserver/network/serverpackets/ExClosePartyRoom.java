package l2trunk.gameserver.network.serverpackets;

public class ExClosePartyRoom extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new ExClosePartyRoom();

    @Override
    protected void writeImpl() {
        writeEx(0x09);
    }
}