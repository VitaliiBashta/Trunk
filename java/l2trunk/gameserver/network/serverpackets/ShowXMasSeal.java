package l2trunk.gameserver.network.serverpackets;

public final class ShowXMasSeal extends L2GameServerPacket {
    private final int item;

    public ShowXMasSeal(int item) {
        this.item = item;
    }

    @Override
    protected void writeImpl() {
        writeC(0xf8);
        writeD(item);
    }
}