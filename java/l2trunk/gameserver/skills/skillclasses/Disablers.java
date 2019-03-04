package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

public final class Disablers extends Skill {
    private final boolean skillInterrupt;
    private final int staticTime;

    public Disablers(StatsSet set) {
        super(set);
        skillInterrupt = set.isSet("skillInterrupt");
        staticTime = set.getInteger("staticTime") * 1000;
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        Creature realTarget;
        boolean reflected;

            if (target != null) {
                reflected = target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                if (skillInterrupt) {
                    if (realTarget.getCastingSkill() != null && !realTarget.getCastingSkill().isMagic() && !realTarget.isRaid())
                        realTarget.abortCast(false, true);
                    if (!realTarget.isRaid())
                        realTarget.abortAttack(true, true);
                }

                getEffects(activeChar, target, activateRate > 0, false, staticTime, 1, reflected);
            }
    }
}