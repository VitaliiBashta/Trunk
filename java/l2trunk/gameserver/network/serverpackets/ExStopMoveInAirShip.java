package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Location;

public class ExStopMoveInAirShip extends L2GameServerPacket {
    private final int char_id;
    private final int boat_id;
    private final int char_heading;
    private final Location _loc;

    public ExStopMoveInAirShip(Player cha) {
        char_id = cha.getObjectId();
        boat_id = cha.getBoat().getObjectId();
        _loc = cha.getInBoatPosition();
        char_heading = cha.getHeading();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x6E);

        writeD(char_id);
        writeD(boat_id);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(char_heading);
    }
}