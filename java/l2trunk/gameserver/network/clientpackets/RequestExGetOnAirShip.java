package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.utils.Location;

//@Deprecated
public class RequestExGetOnAirShip extends L2GameClientPacket {
    private final Location loc = new Location();
    private int _shipId;

    @Override
    protected void readImpl() {
        loc.x = readD();
        loc.y = readD();
        loc.z = readD();
        _shipId = readD();
    }

    @Override
    protected void runImpl() {
		/*L2Player activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;
		L2AirShip boat = (L2AirShip) L2VehicleManager.INSTANCE().getBoat(_shipId);
		if (boat == null)
			return;
		activeChar.stopMove();
		activeChar.setBoat(boat);
		activeChar.setInBoatPosition(loc);
		activeChar.setLoc(boat.getTerritory());
		activeChar.broadcastPacket(new ExGetOnAirShip(activeChar, boat, loc)); */
    }
}