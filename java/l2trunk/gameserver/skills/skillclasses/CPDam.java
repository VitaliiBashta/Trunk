package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

import java.util.List;

public final class CPDam extends Skill {
    public CPDam(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (activeChar.getChargedSoulShot() && isSSPossible())
            activeChar.unChargeShots(false);

        for (Creature target : targets)
            if (target != null) {
                if (target.isDead())
                    continue;

                target.doCounterAttack(this, activeChar, false);

                boolean reflected = target.checkReflectSkill(activeChar, this);
                Creature realTarget = reflected ? activeChar : target;

                if (realTarget.isCurrentCpZero())
                    continue;

                double damage = power * realTarget.getCurrentCp();

                if (damage < 1)
                    damage = 1;

                realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);

                getEffects(activeChar, target, activateRate() > 0, false, reflected);
            }
    }
}