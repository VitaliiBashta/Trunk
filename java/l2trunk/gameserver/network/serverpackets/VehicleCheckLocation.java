package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class VehicleCheckLocation extends L2GameServerPacket {
    private final int _boatObjectId;
    private final Location _loc;

    public VehicleCheckLocation(Boat instance) {
        _boatObjectId = instance.getObjectId();
        _loc = instance.getLoc();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x6d);
        writeD(_boatObjectId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_loc.h);
    }
}