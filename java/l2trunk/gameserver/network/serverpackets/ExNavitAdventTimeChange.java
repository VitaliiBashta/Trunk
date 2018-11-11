package l2trunk.gameserver.network.serverpackets;

public class ExNavitAdventTimeChange extends L2GameServerPacket {
    private final int _active;
    private final int _time;

    public ExNavitAdventTimeChange(boolean active, int time) {
        _active = active ? 1 : 0;
        _time = 14400 - time;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0xE1);
        writeC(_active);
        writeD(_time); // in minutes
    }
}
