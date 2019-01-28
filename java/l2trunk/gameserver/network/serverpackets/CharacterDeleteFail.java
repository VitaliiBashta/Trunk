package l2trunk.gameserver.network.serverpackets;

public final class CharacterDeleteFail extends L2GameServerPacket {
    public static final int REASON_DELETION_FAILED = 0x01;
    public static final int REASON_YOU_MAY_NOT_DELETE_CLAN_MEMBER = 0x02;
    public static final int REASON_CLAN_LEADERS_MAY_NOT_BE_DELETED = 0x03;
    private final int error;

    public CharacterDeleteFail(int error) {
        this.error = error;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x1e);
        writeD(error);
    }
}