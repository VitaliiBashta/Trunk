package l2trunk.gameserver.network.serverpackets;

public final class ExChangeNpcState extends L2GameServerPacket {
    private final int objId;
    private final int state;

    public ExChangeNpcState(int objId, int state) {
        this.objId = objId;
        this.state = state;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xBE);
        writeD(objId);
        writeD(state);
    }
}
