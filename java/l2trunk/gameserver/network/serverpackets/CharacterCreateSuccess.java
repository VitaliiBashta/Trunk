package l2trunk.gameserver.network.serverpackets;

public final class CharacterCreateSuccess extends L2GameServerPacket {
    public static final L2GameServerPacket STATIC = new CharacterCreateSuccess();

    @Override
    protected final void writeImpl() {
        writeC(0x0f);
        writeD(0x01);
    }
}