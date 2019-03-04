package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

public final class ExFishingStartCombat extends L2GameServerPacket {
    private final int time;
    private final int hp;
    private final int lureType;
    private final int deceptiveMode;
    private final int mode;
    private final int charObjId;

    public ExFishingStartCombat(Creature character, int time, int hp, int mode, int lureType, boolean deceptiveMode) {
        charObjId = character.objectId();
        this.time = time;
        this.hp = hp;
        this.mode = mode;
        this.lureType = lureType;
        this.deceptiveMode = deceptiveMode ? 1 :0;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x27);

        writeD(charObjId);
        writeD(time);
        writeD(hp);
        writeC(mode); // mode: 0 = resting, 1 = fighting
        writeC(lureType); // 0 = newbie lure, 1 = normal lure, 2 = night lure
        writeC(deceptiveMode); // Fish Deceptive Mode: 0 = no, 1 = yes
    }
}