package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

import java.util.List;

public final class AIeffects extends Skill {
    public AIeffects(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null)
                getEffects(activeChar, target, activateRate() > 0, false);

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}
