package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.model.instances.residences.SiegeFlagInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.ConditionTargetRelation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ChainHeal extends Skill {
    private final List<Integer> healPercents;
    private final int healRadius;
    private final int maxTargets;

    public ChainHeal(StatsSet set) {
        super(set);
        healRadius = set.getInteger("healRadius", 350);
        List<String> params = List.of(set.getString("healPercents", "").split(";"));
        maxTargets = params.size();
        healPercents = params.stream()
                .map(NumberUtils::toInt)
                .collect(Collectors.toList());
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int curTarget = 0;
        for (Creature target : targets)
            if (target != null) {
                if (!target.isHealBlocked()) {
                    // if it is in duel
                    if (!(target instanceof Player) || !((Player)activeChar).isInDuel() || !((Playable)target).getPlayer().isInDuel()) {
                        getEffects(activeChar, target, activateRate > 0, false);

                        double hp = (healPercents.get(curTarget) * target.getMaxHp()) / 100.;
                        int addToHp = (int) Math.max(0, Math.min(hp, ((target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp()) / 100) - target.getCurrentHp()));

                        if (addToHp > 0) target.setCurrentHp(addToHp + target.getCurrentHp(), false);

                        if (target instanceof Player)
                            if (activeChar != target)
                                target.sendPacket(new SystemMessage(SystemMessage.XS2S_HP_HAS_BEEN_RESTORED_BY_S1).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
                            else {
                                activeChar.sendPacket(new SystemMessage(SystemMessage.S1_HPS_HAVE_BEEN_RESTORED).addNumber(Math.round(addToHp)));
                            }

                        curTarget++;
                    }

                }

            }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }

    @Override
    public List<Creature> getTargets(Creature activeChar, Creature aimingTarget, boolean forceUse) {
        if ( aimingTarget.getAroundCharacters(healRadius, healRadius).count() == 0)
            return new ArrayList<>();

        return Stream.concat(Stream.of( aimingTarget),aimingTarget.getAroundCharacters(healRadius, healRadius))
                .filter(target -> !target.isHealBlocked())
                .filter(target -> ConditionTargetRelation.getRelation(activeChar, target) == ConditionTargetRelation.Relation.Friend)
                .sorted(Comparator.comparingDouble(t  -> t.getCurrentHp() / t.getMaxHp()))
                .limit(maxTargets)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (target instanceof MonsterInstance ) return false;

        if (target == null || target instanceof DoorInstance || target instanceof SiegeFlagInstance)
            return false;

        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

}