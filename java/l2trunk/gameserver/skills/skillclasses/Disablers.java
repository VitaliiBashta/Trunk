package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

import java.util.List;

public final class Disablers extends Skill {
    private final boolean _skillInterrupt;
    private final int _staticTime;

    public Disablers(StatsSet set) {
        super(set);
        _skillInterrupt = set.getBool("skillInterrupt", false);
        _staticTime = set.getInteger("staticTime", 0) * 1000;
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        Creature realTarget;
        boolean reflected;

            if (target != null) {
                reflected = target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                if (_skillInterrupt) {
                    if (realTarget.getCastingSkill() != null && !realTarget.getCastingSkill().isMagic() && !realTarget.isRaid())
                        realTarget.abortCast(false, true);
                    if (!realTarget.isRaid())
                        realTarget.abortAttack(true, true);
                }

                getEffects(activeChar, target, activateRate > 0, false, _staticTime, 1, reflected);
            }
    }
}