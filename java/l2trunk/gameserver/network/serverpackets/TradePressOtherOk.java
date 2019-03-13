package l2trunk.gameserver.network.serverpackets;

public final class TradePressOtherOk extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new TradePressOtherOk();

    @Override
    protected final void writeImpl() {
        writeC(0x82);
    }
}