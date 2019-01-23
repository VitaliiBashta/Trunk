package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Formulas.AttackInfo;

import java.util.List;

public final class ChargeSoul extends Skill {
    private final int numSouls;

    public ChargeSoul(StatsSet set) {
        super(set);
        numSouls = set.getInteger("numSouls", level);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!activeChar.isPlayer())
            return;

        boolean ss = activeChar.getChargedSoulShot() && isSSPossible();
        if (ss && targetType != SkillTargetType.TARGET_SELF)
            activeChar.unChargeShots(false);

        Creature realTarget;
        boolean reflected;

        for (Creature target : targets)
            if (target != null) {
                if (target.isDead())
                    continue;

                reflected = target != activeChar && target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                if (getPower() > 0) {// Если == 0 значит скилл "отключен"
                    AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, ss, false);

                    if (info.lethal_dmg > 0)
                        realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);

                    realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, false, true, false, false, true);
                    if (!reflected)
                        realTarget.doCounterAttack(this, activeChar, false);
                }

                if (realTarget.isPlayable() || realTarget.isMonster())
                    activeChar.setConsumedSouls(activeChar.getConsumedSouls() + numSouls, null);

                getEffects(activeChar, target, activateRate() > 0, false, reflected);
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}