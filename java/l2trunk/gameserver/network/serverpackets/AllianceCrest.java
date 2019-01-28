package l2trunk.gameserver.network.serverpackets;

public class AllianceCrest extends L2GameServerPacket {
    private final int crestId;
    private final byte[] data;

    public AllianceCrest(int crestId, byte[] data) {
        this.crestId = crestId;
        this.data = data;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xaf);
        writeD(crestId);
        writeD(data.length);
        writeB(data);
    }
}