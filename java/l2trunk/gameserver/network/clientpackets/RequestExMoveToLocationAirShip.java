package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;

/**
 * Format: d d|dd
 */
public final class RequestExMoveToLocationAirShip extends L2GameClientPacket {
    private int moveType;
    private int param1, param2;

    @Override
    protected void readImpl() {
        moveType = readD();
        switch (moveType) {
            case 4: // AirShipTeleport
                param1 = readD() + 1;
                break;
            case 0: // Free move
                param1 = readD();
                param2 = readD();
                break;
            case 2: // Up
                readD(); //?
                readD(); //?
                break;
            case 3: //Down
                readD(); //?
                readD(); //?
                break;
        }
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null || player.getBoat() == null || !(player.getBoat() instanceof ClanAirShip))
            return;

        ClanAirShip airship = (ClanAirShip) player.getBoat();
        if (airship.getDriver() == player)
            switch (moveType) {
                case 4: // AirShipTeleport
                    airship.addTeleportPoint(player, param1);
                    break;
                case 0: // Free move
                    if (!airship.isCustomMove())
                        break;
                    airship.moveToLocation(airship.getLoc().setX(param1).setY(param2), 0, false);
                    break;
                case 2: // Up
                    if (!airship.isCustomMove())
                        break;
                    airship.moveToLocation(airship.getLoc().addZ(100), 0, false);
                    break;
                case 3: // Down
                    if (!airship.isCustomMove())
                        break;
                    airship.moveToLocation(airship.getLoc().addZ(-100), 0, false);
                    break;
            }
    }
}