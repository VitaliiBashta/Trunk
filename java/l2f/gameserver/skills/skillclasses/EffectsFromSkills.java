package l2f.gameserver.skills.skillclasses;

import l2f.gameserver.model.Creature;
import l2f.gameserver.model.Skill;
import l2f.gameserver.templates.StatsSet;

import java.util.List;

public class EffectsFromSkills extends Skill {
    public EffectsFromSkills(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        for (Creature target : targets)
            if (target != null)
                for (AddedSkill as : getAddedSkills())
                    as.getSkill().getEffects(activeChar, target, false, false);
    }
}