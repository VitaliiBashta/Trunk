package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.Formulas;

import java.util.List;

public final class CurseDivinity extends Skill {
    public CurseDivinity(StatsSet set) {
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

                if (!target.isPlayer())
                    continue;

                reflected = target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                int buffCount = target.getEffectList().getAllEffects().size();
                double damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
                if (damage >= 1) {
                    damage = damage + (power * 0.1 + power * 0.254 * buffCount);
                    realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
                }


                getEffects(activeChar, target, activateRate() > 0, false, reflected);
            }

        if (isSuicideAttack)
            activeChar.doDie(null);
        else if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}