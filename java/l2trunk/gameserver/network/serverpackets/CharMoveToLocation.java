package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;

public final class CharMoveToLocation extends L2GameServerPacket {
    private final int _objectId;
    private final Location _current;
    private int _client_z_shift;
    private Location _destination;

    public CharMoveToLocation(Creature cha) {
        _objectId = cha.objectId();
        _current = cha.getLoc();
        _destination = cha.getDestination();
        if (!cha.isFlying())
            _client_z_shift = Config.CLIENT_Z_SHIFT;
        if (cha.isInWater())
            _client_z_shift += Config.CLIENT_Z_SHIFT;

        if (_destination == null) {
            Log.debug("CharMoveToLocation: desc is null, but moving. L2Character: " + cha.objectId() + ":" + cha.getName() + "; Loc: " + _current);
            _destination = _current;
        }
    }

    public CharMoveToLocation(int objectId, Location from, Location to) {
        _objectId = objectId;
        _current = from;
        _destination = to;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x2f);

        writeD(_objectId);

        writeD(_destination.x);
        writeD(_destination.y);
        writeD(_destination.z + _client_z_shift);

        writeD(_current.x);
        writeD(_current.y);
        writeD(_current.z + _client_z_shift);
    }
}