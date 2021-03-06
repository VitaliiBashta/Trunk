package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class ExMoveToTargetInAirShip extends L2GameServerPacket {
    private final int char_id;
    private final int boat_id;
    private final int target_id;
    private final int _dist;
    private final Location _loc;

    public ExMoveToTargetInAirShip(Player cha, Boat boat, int targetId, int dist, Location origin) {
        char_id = cha.objectId();
        boat_id = boat.objectId();
        target_id = targetId;
        _dist = dist;
        _loc = origin;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x71);

        writeD(char_id); // ID:%d
        writeD(target_id); // TargetID:%d
        writeD(_dist); //Dist:%d
        writeD(_loc.y); //OriginX:%d
        writeD(_loc.z); //OriginY:%d
        writeD(_loc.h); //OriginZ:%d
        writeD(boat_id); //AirShipID:%d
    }
}