package l2trunk.gameserver.network.serverpackets;

public class ExRequestHackShield extends L2GameServerPacket {
    @Override
    protected final void writeImpl() {
        writeEx(0x49);
    }
}