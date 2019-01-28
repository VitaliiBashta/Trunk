package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.Formulas;

import java.util.List;

public final class MDam extends Skill {
    public MDam(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int sps = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;

        Creature realTarget;
        boolean reflected;

        for (Creature target : targets)
            if (target != null) {
                if (target.isDead())
                    continue;

                reflected = target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                double damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
                if (damage >= 1)
                    realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);

                getEffects(activeChar, target, getActivateRate() > 0, false, reflected);
            }

        if (isSuicideAttack)
            activeChar.doDie(null);
        else if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}