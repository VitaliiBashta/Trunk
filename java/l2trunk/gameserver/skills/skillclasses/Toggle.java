package l2trunk.gameserver.skills.skillclasses;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.templates.StatsSet;

import java.util.List;

public class Toggle extends Skill {
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

        getEffects(activeChar, activeChar, getActivateRate() > 0, false);
    }
}
