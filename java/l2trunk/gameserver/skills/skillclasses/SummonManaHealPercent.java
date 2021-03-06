package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;

import java.util.List;

public final class SummonManaHealPercent extends Skill {
    private final boolean ignoreMpEff;

    public SummonManaHealPercent(StatsSet set) {
        super(set);
        ignoreMpEff = set.isSet("ignoreMpEff");
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {
                getEffects(activeChar, target, activateRate > 0, false);

                double mp = power * target.getMaxMp() / 100.;
                double newMp = mp * (!ignoreMpEff ? target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., activeChar, this) : 100.) / 100.;
                double addToMp = Math.max(0, Math.min(newMp, target.calcStat(Stats.MP_LIMIT, null, null) * target.getMaxMp() / 100. - target.getCurrentMp()));

                if (addToMp > 0)
                    target.setCurrentMp(target.getCurrentMp() + addToMp);
                if (target instanceof Player)
                    if (activeChar != target)
                        target.sendPacket(new SystemMessage2(SystemMsg.S2_MP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addInteger(Math.round(addToMp)));
                    else
                        activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(addToMp)));
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}