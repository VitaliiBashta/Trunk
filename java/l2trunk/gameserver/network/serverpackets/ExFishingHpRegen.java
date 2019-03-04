package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

/**
 * Format (ch)dddcccd
 * d: cahacter oid
 * d: time left
 * d: fish hp
 * c:
 * c:
 * c: 00 if fish gets damage 02 if fish regens
 * d:
 */
public final class ExFishingHpRegen extends L2GameServerPacket {
    private final int time;
    private final int fishHP;
    private final int hPmode;
    private final int anim;
    private final int goodUse;
    private final int penalty;
    private final int hpBarColor;
    private final int charObjId;

    public ExFishingHpRegen(Creature character, int time, int fishHP, int hPmode, int GoodUse, int anim, int penalty, boolean hpBarColor) {
        charObjId = character.objectId();
        this.time = time;
        this.fishHP = fishHP;
        this.hPmode = hPmode;
        goodUse = GoodUse;
        this.anim = anim;
        this.penalty = penalty;
        this.hpBarColor = hpBarColor ? 1 : 0;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x28);
        writeD(charObjId);
        writeD(time);
        writeD(fishHP);
        writeC(hPmode); // 0 = HP stop, 1 = HP raise
        writeC(goodUse); // 0 = none, 1 = success, 2 = failed
        writeC(anim); // Anim: 0 = none, 1 = reeling, 2 = pumping
        writeD(penalty); // Penalty
        writeC(hpBarColor); // 0 = normal hp bar, 1 = purple hp bar

    }
}