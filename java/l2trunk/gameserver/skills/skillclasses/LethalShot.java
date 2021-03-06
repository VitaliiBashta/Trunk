package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Formulas.AttackInfo;

import java.util.List;

public final class LethalShot extends Skill {
    public LethalShot(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
        if (ss)
            activeChar.unChargeShots(false);

        Creature realTarget;
        boolean reflected;

        for (Creature target : targets)
            if (target != null) {
                if (target.isDead())
                    continue;

                reflected = target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                if (power > 0) { // If == 0 means skill "disabled"
                    AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, ss, false);

                    if (info.lethal_dmg > 0)
                        realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);

                    realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true);
                    if (!reflected)
                        realTarget.doCounterAttack(this, activeChar, false);
                }

                getEffects(activeChar, target, activateRate > 0, false, reflected);
            }
    }
}
