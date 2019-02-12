package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.utils.Location;

/**
 * format  dddd
 * <p>
 * sample
 * 0000: 3a  69 08 10 48  02 c1 00 00  f7 56 00 00  89 ea ff    :i..H.....V.....
 * 0010: ff  0c b2 d8 61                                     ....a
 */
public final class TeleportToLocation extends L2GameServerPacket {
    private final int _targetId;
    private final Location _loc;

    public TeleportToLocation(Creature cha, Location loc) {
        _targetId = cha.objectId();
        _loc = loc;
    }

    public TeleportToLocation(Creature cha, int x, int y, int z) {
        _targetId = cha.objectId();
        _loc = new Location(x, y, z, cha.getHeading());
    }

    @Override
    protected final void writeImpl() {
        writeC(0x22);
        writeD(_targetId);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z + Config.CLIENT_Z_SHIFT);
        writeD(0x00); //IsValidation
        writeD(_loc.h);
    }
}