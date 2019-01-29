package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

import java.util.List;

public final class SPHeal extends Skill {
    public SPHeal(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final Creature activeChar, final Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!activeChar.isPlayer())
            return false;

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null) {
                target.getPlayer().addExpAndSp(0, (long) power);

                getEffects(activeChar, target, activateRate > 0, false);
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}
