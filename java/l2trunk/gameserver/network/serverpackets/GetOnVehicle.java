package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.utils.Location;

public class GetOnVehicle extends L2GameServerPacket {
    private final int _playerObjectId;
    private final int _boatObjectId;
    private final Location _loc;

    public GetOnVehicle(Player activeChar, Boat boat, Location loc) {
        _loc = loc;
        _playerObjectId = activeChar.getObjectId();
        _boatObjectId = boat.getObjectId();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x6e);
        writeD(_playerObjectId);
        writeD(_boatObjectId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
    }
}