package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class ExMoveToLocationInAirShip extends L2GameServerPacket {
    private final int char_id;
    private final int boat_id;
    private final Location _origin;
    private final Location _destination;

    public ExMoveToLocationInAirShip(Player cha, Boat boat, Location origin, Location destination) {
        char_id = cha.objectId();
        boat_id = boat.objectId();
        _origin = origin;
        _destination = destination;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x6D);
        writeD(char_id);
        writeD(boat_id);

        writeD(_destination.x);
        writeD(_destination.y);
        writeD(_destination.z);
        writeD(_origin.x);
        writeD(_origin.y);
        writeD(_origin.z);
    }
}