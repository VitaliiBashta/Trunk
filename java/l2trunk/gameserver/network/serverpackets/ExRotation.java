package l2trunk.gameserver.network.serverpackets;

public class ExRotation extends L2GameServerPacket {
    private final int _charObjId;
    private final int _degree;

    public ExRotation(int charId, int degree) {
        _charObjId = charId;
        _degree = degree;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xC1);
        writeD(_charObjId);
        writeD(_degree);
    }
}
