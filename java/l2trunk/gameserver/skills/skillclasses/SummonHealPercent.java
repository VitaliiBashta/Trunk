package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;

import java.util.List;

public final class SummonHealPercent extends Skill {
    private final boolean ignoreHpEff;

    public SummonHealPercent(StatsSet set) {
        super(set);
        ignoreHpEff = set.isSet("ignoreHpEff");
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {
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

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}