package l2trunk.gameserver.network.serverpackets;

public final class EventTrigger extends L2GameServerPacket {
    private final int trapId;
    private final boolean active;

    public EventTrigger(int trapId, boolean active) {
        this.trapId = trapId;
        this.active = active;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xCF);
        writeD(trapId); // trap object id
        writeC(active ? 1 : 0); // trap activity 1 or 0
    }
}