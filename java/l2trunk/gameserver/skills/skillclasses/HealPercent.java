package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.residences.SiegeFlagInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;

public class HealPercent extends Skill {
    private final boolean ignoreHpEff;

    public HealPercent(StatsSet set) {
        super(set);
        ignoreHpEff = set.isSet("ignoreHpEff");
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if ((target == null) || target instanceof DoorInstance || target.isRaid() || target.isBoss() || (target instanceof SiegeFlagInstance))
            return false;

        if (player != null && target instanceof MonsterInstance)
            return false;

        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
            if (target != null) {
                if (!target.isHealBlocked() || target == ((Player)activeChar).getPet()) {
                    getEffects(activeChar, target, activateRate > 0, false);

                    double hp = power * target.getMaxHp() / 100.;
                    double newHp = hp * (!ignoreHpEff ? target.calcStat(Stats.HEAL_EFFECTIVNESS, 100., activeChar, this) : 100.) / 100.;
                    double addToHp = Math.max(0, Math.min(newHp, target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));

                    if (addToHp > 0)
                        target.setCurrentHp(addToHp + target.getCurrentHp(), false);
                    if (target instanceof Player)
                        if (activeChar != target)
                            target.sendPacket(new SystemMessage2(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addInteger(Math.round(addToHp)));
                        else
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));
                }

            }
    }
}