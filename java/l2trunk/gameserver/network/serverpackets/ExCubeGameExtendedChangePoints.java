package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

/**
 * Format: (chd) dddddd
 * d: time left
 * d: blue points
 * d: red points
 * d: team
 * d: getPlayer object id
 * d: getPlayer points
 */
public class ExCubeGameExtendedChangePoints extends L2GameServerPacket {
    private final int _timeLeft;
    private final int _bluePoints;
    private final int _redPoints;
    private final boolean _isRedTeam;
    private final int _objectId;
    private final int _playerPoints;

    public ExCubeGameExtendedChangePoints(int timeLeft, int bluePoints, int redPoints, boolean isRedTeam, Player player, int playerPoints) {
        _timeLeft = timeLeft;
        _bluePoints = bluePoints;
        _redPoints = redPoints;
        _isRedTeam = isRedTeam;
        _objectId = player.objectId();
        _playerPoints = playerPoints;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x98);
        writeD(0x00);

        writeD(_timeLeft);
        writeD(_bluePoints);
        writeD(_redPoints);

        writeD(_isRedTeam ? 0x01 : 0x00);
        writeD(_objectId);
        writeD(_playerPoints);
    }
}