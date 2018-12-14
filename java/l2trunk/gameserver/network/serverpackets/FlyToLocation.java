package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.utils.Location;

public final class FlyToLocation extends L2GameServerPacket {
    private final FlyType type;
    private final int chaObjId;
    private final Location loc;
    private final Location destLoc;

    public FlyToLocation(Creature cha, Location destLoc, FlyType type) {
        this.destLoc = destLoc;
        this.type = type;
        chaObjId = cha.getObjectId();
        loc = cha.getLoc();
    }

    @Override
    protected void writeImpl() {
        writeC(0xd4);
        writeD(chaObjId);
        writeD(destLoc.x);
        writeD(destLoc.y);
        writeD(destLoc.z);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(type.ordinal());
    }

    public enum FlyType {
        THROW_UP,
        THROW_HORIZONTAL,
        DUMMY,
        CHARGE,
        NONE
    }
}