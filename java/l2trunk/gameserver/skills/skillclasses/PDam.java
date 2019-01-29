package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.FinishRotating;
import l2trunk.gameserver.network.serverpackets.StartRotating;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Formulas.AttackInfo;

import java.util.List;

public final class PDam extends Skill {
    private final boolean onCrit;
    private final boolean directHp;
    private final boolean turner;
    private final boolean blow;

    public PDam(StatsSet set) {
        super(set);
        onCrit = set.getBool("onCrit", false);
        directHp = set.getBool("directHp", false);
        turner = set.getBool("turner", false);
        blow = set.getBool("blow", false);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        boolean ss = activeChar.getChargedSoulShot() && isSSPossible();

        Creature realTarget;
        boolean reflected;

        for (Creature target : targets)
            if (target != null && !target.isDead()) {
                if (turner && !target.isInvul()) {
                    target.broadcastPacket(new StartRotating(target, target.getHeading(), 1, 65535));
                    target.broadcastPacket(new FinishRotating(target, activeChar.getHeading(), 65535));
                    target.setHeading(activeChar.getHeading());
                    target.sendPacket(new SystemMessage2(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(displayId, getDisplayLevel()));
                }

                reflected = target.checkReflectSkill(activeChar, this);
                realTarget = reflected ? activeChar : target;

                AttackInfo info = Formulas.calcPhysDam(activeChar, realTarget, this, false, blow, ss, onCrit);

                if (info.lethal_dmg > 0)
                    realTarget.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);

                if (!info.miss || info.damage >= 1)
                    realTarget.reduceCurrentHp(info.damage, activeChar, this, true, true, info.lethal ? false : directHp, true, false, false, power != 0);

                if (!reflected)
                    realTarget.doCounterAttack(this, activeChar, blow);

                getEffects(activeChar, target, activateRate > 0, false, reflected);
            }

        if (isSuicideAttack)
            activeChar.doDie(null);
        else if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}