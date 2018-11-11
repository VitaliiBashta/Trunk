package l2trunk.gameserver.network.serverpackets;

public class ExChangeClientEffectInfo extends L2GameServerPacket {
    private final int _state;

    public ExChangeClientEffectInfo(int state) {
        _state = state;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xC1);
        writeD(0);
        writeD(_state);
    }
}
