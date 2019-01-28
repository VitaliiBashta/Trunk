package l2trunk.gameserver.network.serverpackets;

public final class PledgeCrest extends L2GameServerPacket {
    private final int crestId;
    private final int crestSize;
    private final byte[] data;

    public PledgeCrest(int crestId, byte[] data) {
        this.crestId = crestId;
        this.data = data;
        crestSize = this.data.length;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x6a);
        writeD(crestId);
        writeD(crestSize);
        writeB(data);
    }
}