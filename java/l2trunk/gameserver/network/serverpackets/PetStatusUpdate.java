package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.utils.Location;

public final class PetStatusUpdate extends L2GameServerPacket {
    private final int type;
    private final int obj_id;
    private final int level;
    private final int maxFed;
    private final int curFed;
    private final int maxHp;
    private final int curHp;
    private final int maxMp;
    private final int curMp;
    private final long exp;
    private final long exp_this_lvl;
    private final long exp_next_lvl;
    private final Location _loc;
    private final String title;

    public PetStatusUpdate(final Summon summon) {
        type = summon.getSummonType();
        obj_id = summon.objectId();
        _loc = summon.getLoc();
        title = summon.getTitle();
        curHp = (int) summon.getCurrentHp();
        maxHp = summon.getMaxHp();
        curMp = (int) summon.getCurrentMp();
        maxMp = summon.getMaxMp();
        curFed = summon.getCurrentFed();
        maxFed = summon.getMaxFed();
        level = summon.getLevel();
        exp = summon.getExp();
        exp_this_lvl = summon.getExpForThisLevel();
        exp_next_lvl = summon.getExpForNextLevel();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xb6);
        writeD(type);
        writeD(obj_id);
        writeD(_loc.x);
        writeD(_loc.y);
        writeD(_loc.z);
        writeS(title);
        writeD(curFed);
        writeD(maxFed);
        writeD(curHp);
        writeD(maxHp);
        writeD(curMp);
        writeD(maxMp);
        writeD(level);
        writeQ(exp);
        writeQ(exp_this_lvl);// 0% absolute value
        writeQ(exp_next_lvl);// 100% absolute value
    }
}