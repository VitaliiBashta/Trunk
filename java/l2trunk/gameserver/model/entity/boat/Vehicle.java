package l2trunk.gameserver.model.entity.boat;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.templates.CharTemplate;
import l2trunk.gameserver.utils.Location;

public final class Vehicle extends Boat {
    public Vehicle(int objectId, CharTemplate template) {
        super(objectId, template);
    }

    @Override
    public L2GameServerPacket startPacket() {
        return new VehicleStart(this);
    }

    @Override
    public void validateLocationPacket(Player player) {
        new ValidateLocationInVehicle(player);
    }

    @Override
    public L2GameServerPacket checkLocationPacket() {
        return new VehicleCheckLocation(this);
    }

    @Override
    public L2GameServerPacket infoPacket() {
        return new VehicleInfo(this);
    }

    @Override
    public L2GameServerPacket movePacket() {
        return new VehicleDeparture(this);
    }

    @Override
    public L2GameServerPacket inMovePacket(Player player, Location src, Location desc) {
        return new MoveToLocationInVehicle(player, this, src, desc);
    }

    @Override
    public L2GameServerPacket stopMovePacket() {
        return new StopMove(this);
    }

    @Override
    public L2GameServerPacket inStopMovePacket(Player player) {
        return new StopMoveToLocationInVehicle(player);
    }

    @Override
    public L2GameServerPacket getOnPacket(Player player, Location location) {
        return new GetOnVehicle(player, this, location);
    }

    @Override
    public L2GameServerPacket getOffPacket(Player player, Location location) {
        return new GetOffVehicle(player, this, location);
    }

    @Override
    public void oustPlayers() {
        //
    }

}
