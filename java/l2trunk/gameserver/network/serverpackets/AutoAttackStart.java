package l2trunk.gameserver.network.serverpackets;

public final class AutoAttackStart extends L2GameServerPacket {
    // dh
    private final int targetId;

    public AutoAttackStart(int targetId) {
        this.targetId = targetId;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x25);
        writeD(targetId);
    }
}