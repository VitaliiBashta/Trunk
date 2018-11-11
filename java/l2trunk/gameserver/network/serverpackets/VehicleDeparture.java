package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class VehicleDeparture extends L2GameServerPacket {
    private final int _moveSpeed;
    private final int _rotationSpeed;
    private final int _boatObjId;
    private final Location _loc;

    public VehicleDeparture(Boat boat) {
        _boatObjId = boat.getObjectId();
        _moveSpeed = boat.getMoveSpeed();
        _rotationSpeed = boat.getRotationSpeed();
        _loc = boat.getDestination();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x6c);
        writeD(_boatObjId);
        writeD(_moveSpeed);
        writeD(_rotationSpeed);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
    }
}