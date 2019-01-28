package l2trunk.gameserver.network.serverpackets;

public final class ActionFail extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new ActionFail();

    @Override
    protected final void writeImpl() {
        writeC(0x1f);
    }
}