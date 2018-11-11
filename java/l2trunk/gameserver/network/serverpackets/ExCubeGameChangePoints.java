package l2trunk.gameserver.network.serverpackets;

/**
 * Format: (chd) ddd
 * d: time left
 * d: blue points
 * d: red points
 */
public class ExCubeGameChangePoints extends L2GameServerPacket {
    private final int _timeLeft;
    private final int _bluePoints;
    private final int _redPoints;

    public ExCubeGameChangePoints(int timeLeft, int bluePoints, int redPoints) {
        _timeLeft = timeLeft;
        _bluePoints = bluePoints;
        _redPoints = redPoints;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x98);
        writeD(0x02);

        writeD(_timeLeft);
        writeD(_bluePoints);
        writeD(_redPoints);
    }
}