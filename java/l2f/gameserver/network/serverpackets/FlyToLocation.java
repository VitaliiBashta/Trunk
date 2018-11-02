package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Creature;
import l2f.gameserver.utils.Location;

public class FlyToLocation extends L2GameServerPacket {
    private final FlyType _type;
    private int _chaObjId;
    private Location _loc;
    private Location _destLoc;

    public FlyToLocation(Creature cha, Location destLoc, FlyType type) {
        _destLoc = destLoc;
        _type = type;
        _chaObjId = cha.getObjectId();
        _loc = cha.getLoc();
    }

    @Override
    protected void writeImpl() {
        writeC(0xd4);
        writeD(_chaObjId);
        writeD(_destLoc.x);
        writeD(_destLoc.y);
        writeD(_destLoc.z);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeD(_type.ordinal());
    }

    public enum FlyType {
        THROW_UP,
        THROW_HORIZONTAL,
        DUMMY,
        CHARGE,
        NONE
    }
}