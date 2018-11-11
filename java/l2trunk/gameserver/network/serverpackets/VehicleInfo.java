package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class VehicleInfo extends L2GameServerPacket {
    private final int _boatObjectId;
    private final Location _loc;

    public VehicleInfo(Boat boat) {
        _boatObjectId = boat.getObjectId();
        _loc = boat.getLoc();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x60);
        writeD(_boatObjectId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_loc.h);
    }
}