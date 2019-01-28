package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Stats;

import java.util.List;

public final class ManaDam extends Skill {
    public ManaDam(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int sps = 0;
        if (isSSPossible())
            sps = activeChar.getChargedSpiritShot();

        for (Creature target : targets) {
            if (target != null) {
                if (target.isDead())
                    continue;

                double mpBefore = target.getCurrentMp();

                double mAtk = activeChar.getMAtk(target, this);
                if (sps == 2)
                    mAtk *= 4;
                else if (sps == 1)
                    mAtk *= 2;

                double mDef = target.getMDef(activeChar, this);
                if (mDef < 1.)
                    mDef = 1.;

                double damage = Math.sqrt(mAtk) * power * (target.getMaxMp() / 97.) / mDef;

                boolean crit = Formulas.calcMCrit(activeChar.getMagicCriticalRate(target, this));
                if (crit) {
                    activeChar.sendPacket(SystemMsg.MAGIC_CRITICAL_HIT);
                    damage *= activeChar.calcStat(Stats.MCRITICAL_DAMAGE, activeChar.isPlayable() && target.isPlayable() ? 2.5 : 3., target, this);
                }
                target.reduceCurrentMp(damage, activeChar);
                target.sendMessage(activeChar.getName() + " has stolen " + (int) (mpBefore - target.getCurrentMp()) + " MP from you!");
                activeChar.sendMessage("You have stolen " + (int) (mpBefore - target.getCurrentMp()) + " MP from " + target.getName());

                getEffects(activeChar, target, activateRate() > 0, false);
            }
        }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}