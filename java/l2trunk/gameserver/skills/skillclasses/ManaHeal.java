package l2trunk.gameserver.skills.skillclasses;


import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;

public final class ManaHeal extends Skill {

    public ManaHeal(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {

        if (!target.isHealBlocked()) {
            double newMp = activeChar == target ? power : Math.min(power * 1.7, power + target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 0., activeChar, this));

            // Treatment differences leveled at RECHARGER. difference skill occupation and target occupation.
            // 1013 = id skill recharge. For servitors not verified decrease mana until left as is.
            if (magicLevel > 0 && activeChar != target) {
                int diff = target.getLevel() - magicLevel;
                if (diff > 5)
                    if (diff < 20)
                        newMp = newMp - (newMp * 0.103 * (diff - 5));
                    else
                        newMp = 0;
            }

            if (newMp != 0) {
                double addToMp = Math.max(0, Math.min(newMp, target.calcStat(Stats.MP_LIMIT, null, null) * target.getMaxMp() / 100. - target.getCurrentMp()));

                if (addToMp > 0)
                    target.setCurrentMp(addToMp + target.getCurrentMp());
                if (target instanceof Player)
                    if (activeChar != target)
                        target.sendPacket(new SystemMessage2(SystemMsg.S2_MP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addInteger(Math.round(addToMp)));
                    else
                        activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(addToMp)));
                getEffects(activeChar, target, activateRate > 0, false);
            } else {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(id, getDisplayLevel()));
                getEffects(activeChar, target, activateRate > 0, false);
            }

        }

    }
}