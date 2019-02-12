package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Location;

public class StopMoveToLocationInVehicle extends L2GameServerPacket {
    private final int _boatObjectId;
    private final int _playerObjectId;
    private final int _heading;
    private final Location _loc;

    public StopMoveToLocationInVehicle(Player player) {
        _boatObjectId = player.getBoat().objectId();
        _playerObjectId = player.objectId();
        _loc = player.getInBoatPosition();
        _heading = player.getHeading();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x7f);
        writeD(_playerObjectId);
        writeD(_boatObjectId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_heading);
    }
}