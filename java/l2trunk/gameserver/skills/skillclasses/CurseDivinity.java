package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.Formulas;

public final class CurseDivinity extends Skill {
    public CurseDivinity(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        int sps = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;

        Creature realTarget;
        boolean reflected;

        if (target != null) {
            if (!target.isDead()) {
                if (target instanceof Player) {
                    reflected = target.checkReflectSkill(activeChar, this);
                    realTarget = reflected ? activeChar : target;

                    int buffCount = target.getEffectList().getAllEffects().size();
                    double damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
                    if (damage >= 1) {
                        damage = damage + (power * 0.1 + power * 0.254 * buffCount);
                        realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
                    }


                    getEffects(activeChar, target, activateRate > 0, false, reflected);
                }

            }

        }
    }
}