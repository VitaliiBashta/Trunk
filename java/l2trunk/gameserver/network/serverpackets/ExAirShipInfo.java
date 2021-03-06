package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.AirShip;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;
import l2trunk.gameserver.utils.Location;

public class ExAirShipInfo extends L2GameServerPacket {
    private final int _objId;
    private final int _speed1;
    private final int _speed2;
    private final Location _loc;
    private int _fuel;
    private int _maxFuel;
    private int _driverObjId;
    private int _controlKey;

    public ExAirShipInfo(AirShip ship) {
        _objId = ship.objectId();
        _loc = ship.getLoc();
        _speed1 = ship.getRunSpeed();
        _speed2 = ship.getRotationSpeed();
        if (ship instanceof ClanAirShip) {
            _fuel = ((ClanAirShip) ship).getCurrentFuel();
            _maxFuel = ((ClanAirShip) ship).getMaxFuel();
            Player driver = ((ClanAirShip) ship).getDriver();
            _driverObjId = driver == null ? 0 : driver.objectId();
            _controlKey = ((ClanAirShip) ship).getControlKey().objectId();
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x60);

        writeD(_objId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_loc.h);
        writeD(_driverObjId); // object id of getPlayer who control ship
        writeD(_speed1);
        writeD(_speed2);
        writeD(_controlKey);

        if (_controlKey != 0) {
            writeD(0x16e); // Controller X
            writeD(0x00); // Controller Y
            writeD(0x6b); // Controller Z
            writeD(0x15c); // Captain X
            writeD(0x00); // Captain Y
            writeD(0x69); // Captain Z
        } else {
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
        }

        writeD(_fuel); // current fuel
        writeD(_maxFuel); // max fuel
    }
}