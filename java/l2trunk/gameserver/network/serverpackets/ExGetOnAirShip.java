package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class ExGetOnAirShip extends L2GameServerPacket {
    private final int _playerObjectId;
    private final int _boatObjectId;
    private final Location _loc;

    public ExGetOnAirShip(Player cha, Boat boat, Location loc) {
        _playerObjectId = cha.objectId();
        _boatObjectId = boat.objectId();
        _loc = loc;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x63);
        writeD(_playerObjectId);
        writeD(_boatObjectId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
    }
}