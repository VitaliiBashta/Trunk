package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

import java.util.List;

public final class Toggle extends Skill {
    public Toggle(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (activeChar.getEffectList().getEffectsBySkillId(id) != null) {
            activeChar.getEffectList().stopEffect(id);
            activeChar.sendActionFailed();
            return;
        }

        getEffects(activeChar, activeChar, activateRate > 0, false);
    }
}
