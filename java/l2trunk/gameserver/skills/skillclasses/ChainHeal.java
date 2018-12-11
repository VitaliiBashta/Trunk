package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.residences.SiegeFlagInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.ConditionTargetRelation;
import l2trunk.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ChainHeal extends Skill {
    private final List<Integer> _healPercents;
    private final int _healRadius;
    private final int _maxTargets;

    public ChainHeal(StatsSet set) {
        super(set);
        _healRadius = set.getInteger("healRadius", 350);
        List<String> params = Arrays.asList(set.getString("healPercents", "").split(";"));
        _maxTargets = params.size();
        _healPercents = params.stream()
                .map(NumberUtils::toInt)
                .collect(Collectors.toList());
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int curTarget = 0;
        for (Creature target : targets) {
            if (target == null) {
                continue;
            }

            if (target.isHealBlocked()) {
                continue;
            }

            if (target.isPlayer()) {
                // if it is in duel
                if (activeChar.getPlayer().isInDuel() && target.getPlayer().isInDuel()) {
                    continue;
                }
            }

            getEffects(activeChar, target, getActivateRate() > 0, false);

            double hp = (_healPercents.get(curTarget) * target.getMaxHp()) / 100.;
            double addToHp = Math.max(0, Math.min(hp, ((target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp()) / 100.) - target.getCurrentHp()));

            if (addToHp > 0) {
                target.setCurrentHp(addToHp + target.getCurrentHp(), false);
            }

            if (target.isPlayer()) {
                if (activeChar != target) {
                    target.sendPacket(new SystemMessage(SystemMessage.XS2S_HP_HAS_BEEN_RESTORED_BY_S1).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
                } else {
                    activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
                }
            }

            curTarget++;
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }

    @Override
    public List<Creature> getTargets(Creature activeChar, Creature aimingTarget, boolean forceUse) {
        List<Creature> result = new ArrayList<>();
        List<Creature> targets = aimingTarget.getAroundCharacters(_healRadius, _healRadius);
        if (targets == null) {
            return result;
        }

        List<HealTarget> healTargets = new ArrayList<>();
        healTargets.add(new HealTarget(-100.0D, aimingTarget));
        for (Creature target : targets) {
            if ((target == null) || target.isHealBlocked() || target.isCursedWeaponEquipped()
                    || (ConditionTargetRelation.getRelation(activeChar, target) != ConditionTargetRelation.Relation.Friend)
                /*|| target.isAutoAttackable(activeChar)*/) // there are now at anthars let's go to see?okay i will play a bit
            {
                continue;
            }

            double hpPercent = target.getCurrentHp() / target.getMaxHp();
            healTargets.add(new HealTarget(hpPercent, target));
        }

        List<HealTarget> sortedTargets = new ArrayList<>(healTargets);
        sortedTargets.sort( (o1, o2) -> {
            if ((o1 == null) || (o2 == null)) {
                return 0;
            }
            return Double.compare(o1.getHpPercent(), o2.getHpPercent());
        });

        int targetsCount = 0;
        for (HealTarget ht : sortedTargets) {
            result.add(ht.getTarget());
            targetsCount++;
            if (targetsCount >= _maxTargets) {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if ((target == null) || target.isDoor() || target.isRaid() || target.isBoss() || (target instanceof SiegeFlagInstance))
            return false;

        if (activeChar.isPlayable() && target.isMonster()) {
            return false;
        }
        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    private static class HealTarget {
        private final double hpPercent;
        private final Creature target;

        HealTarget(double hpPercent, Creature target) {
            this.hpPercent = hpPercent;
            this.target = target;
        }

        double getHpPercent() {
            return hpPercent;
        }

        Creature getTarget() {
            return target;
        }
    }
}