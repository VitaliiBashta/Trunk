package l2trunk.gameserver.network.serverpackets;

public class HideBoard extends L2GameServerPacket {
    @Override
    protected final void writeImpl() {
        writeC(0x7b);
        writeC(0x00); //c4 1 to show community 00 to hide
    }
}