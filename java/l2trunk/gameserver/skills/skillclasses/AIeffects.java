package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

public final class AIeffects extends Skill {
    public AIeffects(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        getEffects(activeChar, target, activateRate > 0, false);
    }
}
