package l2trunk.gameserver.network.serverpackets;

public final class ExNavitAdventTimeChange extends L2GameServerPacket {
    private final int active;
    private final int time;

    public ExNavitAdventTimeChange(boolean active, int time) {
        this.active = active ? 1 : 0;
        this.time = 14400 - time;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0xE1);
        writeC(active);
        writeD(time); // in minutes
    }
}
