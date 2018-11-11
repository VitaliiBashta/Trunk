package l2trunk.gameserver.network.serverpackets;

public class Ex2ndPasswordVerify extends L2GameServerPacket {
    public static final int PASSWORD_OK = 0x00;
    public static final int PASSWORD_WRONG = 0x01;
    public static final int PASSWORD_BAN = 0x02;

    private final int _wrongTentatives;
    private final int _mode;

    public Ex2ndPasswordVerify(int mode, int wrongTentatives) {
        _mode = mode;
        _wrongTentatives = wrongTentatives;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xE6);
        writeD(_mode);
        writeD(_wrongTentatives);
    }
}
