package l2trunk.gameserver.network.serverpackets;

/**
 * Format: (chd) ddd
 * d: winner team
 */
public final class ExCubeGameEnd extends L2GameServerPacket {
    private final boolean isRedTeamWin;

    public ExCubeGameEnd(boolean isRedTeamWin) {
        this.isRedTeamWin = isRedTeamWin;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x98);
        writeD(0x01);

        writeD(isRedTeamWin ? 0x01 : 0x00);
    }
}