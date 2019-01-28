package l2trunk.gameserver.network.serverpackets;

public final class CameraMode extends L2GameServerPacket {
    private final int mode;

    /**
     * Forces client camera mode change
     *
     * @param mode 0 - third person cam
     *             1 - first person cam
     */
    public CameraMode(int mode) {
        this.mode = mode;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xf7);
        writeD(mode);
    }
}