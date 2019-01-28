package l2trunk.gameserver.network.serverpackets;

public final class CharacterDeleteSuccess extends L2GameServerPacket {
    @Override
    protected final void writeImpl() {
        writeC(0x1d);
    }
}