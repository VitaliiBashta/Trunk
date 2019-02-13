package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.ConditionTargetRelation;

public final class Continuous extends Skill {

    public Continuous(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        // Player holding a cursed weapon can't be buffed and can't buff
        if (skillType == Skill.SkillType.BUFF && target != null) {
            if (target instanceof Playable  && !target.getPlayer().equals(player)) {
                Player pTarget = target.getPlayer();
                if (pTarget.isVarSet("antigrief") && !pTarget.isInOlympiadMode() && ConditionTargetRelation.getRelation(player, pTarget) != ConditionTargetRelation.Relation.Friend)
                    return false;
            }

            if (target.isRaid()) {
                return false;
            }

        }
        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, Creature t) {

        boolean reflected = t.checkReflectSkill(activeChar, this);
        Creature realTarget = reflected ? activeChar : t;

        double mult = 0.01 * realTarget.calcStat(Stats.DEATH_VULNERABILITY, activeChar, this);
        double lethal1 = this.lethal1 * mult;
        double lethal2 = this.lethal2 * mult;

        if (lethal1 > 0 && Rnd.chance(lethal1)) {
            if (realTarget instanceof Player) {
                realTarget.reduceCurrentHp(realTarget.getCurrentCp(), activeChar, this, true, true, false, true, false, false, true);
                realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
                activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
            } else if (realTarget instanceof NpcInstance && !realTarget.isLethalImmune()) {
                realTarget.reduceCurrentHp(realTarget.getCurrentHp() / 2, activeChar, this, true, true, false, true, false, false, true);
                activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
            }
        } else if (lethal2 > 0 && Rnd.chance(lethal2))
            if (realTarget instanceof Player) {
                realTarget.reduceCurrentHp(realTarget.getCurrentHp() + realTarget.getCurrentCp() - 1, activeChar, this, true, true, false, true, false, false, true);
                realTarget.sendPacket(SystemMsg.LETHAL_STRIKE);
                activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
            } else if (realTarget instanceof NpcInstance && !realTarget.isLethalImmune()) {
                realTarget.reduceCurrentHp(realTarget.getCurrentHp() - 1, activeChar, this, true, true, false, true, false, false, true);
                activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
            }

        getEffects(activeChar, t, activateRate > 0, false, reflected);
    }
}