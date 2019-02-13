package l2trunk.gameserver.utils;

import l2trunk.commons.util.Rnd;

public final class NpcLocation extends Location{
    public int npcId;

    public NpcLocation() {

    }

    public NpcLocation(Location loc, int npcId) {
        super(loc.x, loc.y, loc.z, loc.h);
        this.npcId = npcId;
    }

    public NpcLocation(int npcId, int x, int y, int z) {
        super(x,y,z);
        this.npcId = npcId;
    }

    public NpcLocation(int x, int y, int z, int heading, int npcId) {
        super(x, y, z, heading < 0 ? Rnd.get(65536) : heading);
        this.npcId = npcId;
    }

}

