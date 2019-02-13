package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Location;

public class ExValidateLocationInAirShip extends L2GameServerPacket {
    private final int _playerObjectId;
    private final int _boatObjectId;
    private final Location _loc;

    public ExValidateLocationInAirShip(Player cha) {
        _playerObjectId = cha.objectId();
        _boatObjectId = cha.getBoat().objectId();
        _loc = cha.getInBoatPosition();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x6F);

        writeD(_playerObjectId);
        writeD(_boatObjectId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_loc.h);
    }
}