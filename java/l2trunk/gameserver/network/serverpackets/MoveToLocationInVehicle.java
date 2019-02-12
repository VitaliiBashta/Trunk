package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class MoveToLocationInVehicle extends L2GameServerPacket {
    private final int _playerObjectId;
    private final int _boatObjectId;
    private final Location _origin;
    private final Location _destination;

    public MoveToLocationInVehicle(Player cha, Boat boat, Location origin, Location destination) {
        _playerObjectId = cha.objectId();
        _boatObjectId = boat.objectId();
        _origin = origin;
        _destination = destination;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x7e);
        writeD(_playerObjectId);
        writeD(_boatObjectId);
        writeD(_destination.x);
        writeD(_destination.y);
        writeD(_destination.z);
        writeD(_origin.x);
        writeD(_origin.y);
        writeD(_origin.z);
    }
}