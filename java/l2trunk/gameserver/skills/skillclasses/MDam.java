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

        for (Creature target : targets)
            if (target != null && !target.isDead()) {
                boolean reflected = target.checkReflectSkill(activeChar, this);
                Creature realTarget = reflected ? activeChar : target;

                double damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
                if (damage >= 1)
                    realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);

                getEffects(activeChar, target, activateRate > 0, false, reflected);
            }

        if (isSuicideAttack)
            activeChar.doDie(null);
        else if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}