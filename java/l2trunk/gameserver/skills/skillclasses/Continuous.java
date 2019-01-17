package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.ConditionTargetRelation;

import java.util.List;
import java.util.Objects;

public final class Continuous extends Skill {
    private final int _lethal1;
    private final int _lethal2;

    public Continuous(StatsSet set) {
        super(set);
        _lethal1 = set.getInteger("lethal1", 0);
        _lethal2 = set.getInteger("lethal2", 0);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        // Player holding a cursed weapon can't be buffed and can't buff
        if (getSkillType() == Skill.SkillType.BUFF && target != null) {
            if (target != activeChar)
                if (target.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped())
                    return false;

            if (target.isPlayable() && activeChar.isPlayable() && !target.getPlayer().equals(activeChar)) {
                Player pTarget = target.getPlayer();
                if (pTarget.getVarB("antigrief", false) && !pTarget.isInOlympiadMode() && ConditionTargetRelation.getRelation(activeChar, pTarget) != ConditionTargetRelation.Relation.Friend)
                    return false;
            }

            if (target.isRaid()) {
                return false;
            }

        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        targets.stream()
                .filter(Objects::nonNull)
                .forEach(t -> {
                    boolean reflected = t.checkReflectSkill(activeChar, this);
                    Creature realTarget = reflected ? activeChar : t;

                    double mult = 0.01 * realTarget.calcStat(Stats.DEATH_VULNERABILITY, activeChar, this);
                    double lethal1 = _lethal1 * mult;
                    double lethal2 = _lethal2 * mult;

                    if (lethal1 > 0 && Rnd.chance(lethal1)) {
                        if (realTarget.isPlayer()) {
                            realTarget.reduceCurrentHp(realTarget.getCurrentCp(), activeChar, this, true, true, false, true, false, false, true);
                            realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        } else if (realTarget.isNpc() && !realTarget.isLethalImmune()) {
                            realTarget.reduceCurrentHp(realTarget.getCurrentHp() / 2, activeChar, this, true, true, false, true, false, false, true);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        }
                    } else if (lethal2 > 0 && Rnd.chance(lethal2))
                        if (realTarget.isPlayer()) {
                            realTarget.reduceCurrentHp(realTarget.getCurrentHp() + realTarget.getCurrentCp() - 1, activeChar, this, true, true, false, true, false, false, true);
                            realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        } else if (realTarget.isNpc() && !realTarget.isLethalImmune()) {
                            realTarget.reduceCurrentHp(realTarget.getCurrentHp() - 1, activeChar, this, true, true, false, true, false, false, true);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        }

                    getEffects(activeChar, t, getActivateRate() > 0, false, reflected);
                });

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}