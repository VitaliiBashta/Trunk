package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.BoatHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class RequestGetOffVehicle extends L2GameClientPacket {
    private final Location _location = new Location();
    // Format: cdddd
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
        _location.x = readD();
        _location.y = readD();
        _location.z = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Boat boat = BoatHolder.getInstance().getBoat(_objectId);
        if (boat == null || boat.isMoving) {
            player.sendActionFailed();
            return;
        }

        boat.oustPlayer(player, _location, false);
    }
}