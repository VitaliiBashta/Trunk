package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.BoatHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class RequestGetOnVehicle extends L2GameClientPacket {
    private int _objectId;
    private final Location _loc = new Location();

    /**
     * packet type id 0x53 format: cdddd
     */
    @Override
    protected void readImpl() {
        _objectId = readD();
        _loc.x = readD();
        _loc.y = readD();
        _loc.z = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Boat boat = BoatHolder.getInstance().getBoat(_objectId);
        if (boat == null)
            return;

        player._stablePoint = boat.getCurrentWay().getReturnLoc();
        boat.addPlayer(player, _loc);
    }
}