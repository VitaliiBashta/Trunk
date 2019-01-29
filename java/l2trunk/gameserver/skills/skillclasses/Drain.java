package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Formulas.AttackInfo;
import l2trunk.gameserver.stats.Stats;

import java.util.List;

public final class Drain extends Skill {
    private final double absorbAbs;

    public Drain(StatsSet set) {
        super(set);
        absorbAbs = set.getDouble("absorbAbs", 0.f);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int sps = isSSPossible() ? activeChar.getChargedSpiritShot() : 0;
        boolean ss = isSSPossible() && activeChar.getChargedSoulShot();
        final boolean corpseSkill = (targetType == SkillTargetType.TARGET_CORPSE);

        for (Creature target : targets)
            if (target != null) {
                boolean reflected = !corpseSkill && target.checkReflectSkill(activeChar, this);
                Creature realTarget = reflected ? activeChar : target;

                if (power > 0 || absorbAbs > 0) {// Если == 0 значит скилл "отключен"
                    if (realTarget.isDead() && !corpseSkill)
                        continue;

                    double hp = 0.;
                    double targetHp = realTarget.getCurrentHp();

                    if (!corpseSkill) {
                        double damage;
                        if (isMagic())
                            damage = Formulas.calcMagicDam(activeChar, realTarget, this, sps);
                        else {
                            AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, false, ss, false);
                            damage = info.damage;

                            if (info.lethal_dmg > 0)
                                realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
                        }
                        double targetCP = realTarget.getCurrentCp();

                        // Нельзя восстанавливать HP из CP
                        if (damage > targetCP || !realTarget.isPlayer())
                            hp = (damage - targetCP) * absorbPart;

                        realTarget.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
                        if (!reflected)
                            realTarget.doCounterAttack(this, activeChar, false);
                    }

                    if (absorbAbs == 0 && absorbPart == 0)
                        continue;

                    hp += absorbAbs;

                    // Нельзя восстановить больше hp, чем есть у цели.
                    if (hp > targetHp && !corpseSkill)
                        hp = targetHp;

                    double addToHp = Math.max(0, Math.min(hp, activeChar.calcStat(Stats.HP_LIMIT, null, null) * activeChar.getMaxHp() / 100. - activeChar.getCurrentHp()));

                    if (addToHp > 0 && !activeChar.isHealBlocked())
                        activeChar.setCurrentHp(activeChar.getCurrentHp() + addToHp, false);

                    if (realTarget.isDead() && corpseSkill && realTarget.isNpc()) {
                        activeChar.getAI().setAttackTarget(null);
                        ((NpcInstance) realTarget).endDecayTask();
                    }
                }

                getEffects(activeChar, target, activateRate > 0, false, reflected);
            }

        if (isMagic() ? sps != 0 : ss)
            activeChar.unChargeShots(isMagic());
    }
}