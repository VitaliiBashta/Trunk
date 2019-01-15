package l2trunk.gameserver.network.serverpackets;

public final class ExChangeClientEffectInfo extends L2GameServerPacket {
    private final int state;

    public ExChangeClientEffectInfo(int state) {
        this.state = state;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xC1);
        writeD(0);
        writeD(state);
    }
}
