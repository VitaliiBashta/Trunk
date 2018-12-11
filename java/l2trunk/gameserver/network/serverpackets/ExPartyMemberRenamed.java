package l2trunk.gameserver.network.serverpackets;

public final class ExPartyMemberRenamed extends L2GameServerPacket {
    @Override
    protected void writeImpl() {
        writeEx(0xA6);
        // TODO ddd
    }
}