package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.BoatHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class RequestMoveToLocationInVehicle extends L2GameClientPacket {
    private final Location _pos = new Location();
    private final Location _originPos = new Location();
    private int _boatObjectId;

    @Override
    protected void readImpl() {
        _boatObjectId = readD();
        _pos.x = readD();
        _pos.y = readD();
        _pos.z = readD();
        _originPos.x = readD();
        _originPos.y = readD();
        _originPos.z = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        player.isntAfk();

        Boat boat = BoatHolder.getInstance().getBoat(_boatObjectId);
        if (boat == null) {
            player.sendActionFailed();
            return;
        }

        boat.moveInBoat(player, _originPos, _pos);
    }
}