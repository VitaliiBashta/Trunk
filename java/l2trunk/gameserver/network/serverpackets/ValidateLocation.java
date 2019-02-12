package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.utils.Location;


public final class ValidateLocation extends L2GameServerPacket {
    private final int chaObjId;
    private final Location loc;

    public ValidateLocation(Creature cha) {
        chaObjId = cha.objectId();
        loc = cha.getLoc();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x79);

        writeD(chaObjId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(loc.h);
    }
}