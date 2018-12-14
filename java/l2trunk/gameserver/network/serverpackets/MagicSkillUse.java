package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.utils.Location;

public final class MagicSkillUse extends L2GameServerPacket {
    private final int targetId;
    private final int skillId;
    private final int skillLevel;
    private final int hitTime;
    private final int reuseDelay;
    private final int chaId;
    private final Location loc;
    private final Location targetLoc;

    public MagicSkillUse(Creature cha, int skillId) {
        this(cha, skillId, 1, 0, 0);
    }
    public MagicSkillUse(Creature cha, Skill skill) {
        this(cha, skill.getId(), skill.getHitTime(), 0);
    }
    public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel) {
        this(cha, target, skillId, skillLevel, 0, 0);
    }
    public MagicSkillUse(Creature cha, Creature target, int skillId) {
        this(cha, target, skillId, 1, 0, 0);
    }
    public MagicSkillUse(Creature cha, int skillId, int hitTime) {
        this(cha, skillId, 1, hitTime, 0);
    }
    public MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime) {
        this(cha, cha, skillId, skillLevel, hitTime, 0);
    }
    public MagicSkillUse(Creature cha, Creature target, int skillId, int skillLevel, int hitTime, long reuseDelay) {
        chaId = cha.getObjectId();
        targetId = target.getObjectId();
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.hitTime = hitTime;
        this.reuseDelay = (int) reuseDelay;
        loc = cha.getLoc();
        targetLoc = target.getLoc();
    }

    private MagicSkillUse(Creature cha, int skillId, int skillLevel, int hitTime, long reuseDelay) {
        this(cha, cha, skillId, skillLevel, hitTime, reuseDelay);
    }

    @Override
    protected final void writeImpl() {
        Player activeChar = getClient().getActiveChar();

        if (activeChar != null && activeChar.isNotShowBuffAnim() && activeChar.getObjectId() != chaId)
            return;

        writeC(0x48);
        writeD(chaId);
        writeD(targetId);
        writeD(skillId);
        writeD(skillLevel);
        writeD(hitTime);
        writeD(reuseDelay);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(0x00); // unknown
        writeD(targetLoc.x);
        writeD(targetLoc.y);
        writeD(targetLoc.z);
    }
}