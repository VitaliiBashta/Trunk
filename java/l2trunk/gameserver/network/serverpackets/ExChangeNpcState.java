package l2trunk.gameserver.network.serverpackets;

public class ExChangeNpcState extends L2GameServerPacket {
    private final int _objId;
    private final int _state;

    public ExChangeNpcState(int objId, int state) {
        _objId = objId;
        _state = state;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xBE);
        writeD(_objId);
        writeD(_state);
    }
}
