package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class ExStopMoveAirShip extends L2GameServerPacket {
    private final int boat_id;
    private final Location _loc;

    public ExStopMoveAirShip(Boat boat) {
        boat_id = boat.objectId();
        _loc = boat.getLoc();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x66);
        writeD(boat_id);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_loc.h);
    }
}