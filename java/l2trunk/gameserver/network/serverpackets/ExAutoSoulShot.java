package l2trunk.gameserver.network.serverpackets;

public final class ExAutoSoulShot extends L2GameServerPacket {
    private final int itemId;
    private final boolean type;

    public ExAutoSoulShot(int itemId, boolean type) {
        this.itemId = itemId;
        this.type = type;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x0c);

        writeD(itemId);
        writeD(type ? 1 : 0);
    }
}