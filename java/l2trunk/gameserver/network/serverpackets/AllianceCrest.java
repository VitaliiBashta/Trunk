package l2trunk.gameserver.network.serverpackets;

public final class AllianceCrest extends L2GameServerPacket {
    private final int _crestId;
    private final byte[] _data;

    public AllianceCrest(int crestId, byte[] data) {
        _crestId = crestId;
        _data = data;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xaf);
        writeD(_crestId);
        writeD(_data.length);
        writeB(_data);
    }
}