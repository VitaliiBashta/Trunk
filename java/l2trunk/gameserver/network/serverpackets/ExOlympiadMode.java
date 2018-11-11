package l2trunk.gameserver.network.serverpackets;

public class ExOlympiadMode extends L2GameServerPacket {
    // chc
    private final int _mode;

    public ExOlympiadMode(int mode) {
        _mode = mode;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x7c);

        writeC(_mode);
    }
}