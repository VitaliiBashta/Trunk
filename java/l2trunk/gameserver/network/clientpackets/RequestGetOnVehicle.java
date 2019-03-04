package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.BoatHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public final class RequestGetOnVehicle extends L2GameClientPacket {
    private  Location loc;
    private int _objectId;

    /**
     * packet type id 0x53 format: cdddd
     */
    @Override
    protected void readImpl() {
        _objectId = readD();
        loc = Location.of(readD(),readD(),readD());
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Boat boat = BoatHolder.getInstance().getBoat(_objectId);
        if (boat == null)
            return;

        player.stablePoint = boat.getCurrentWay().getReturnLoc();
        boat.addPlayer(player, loc);
    }
}