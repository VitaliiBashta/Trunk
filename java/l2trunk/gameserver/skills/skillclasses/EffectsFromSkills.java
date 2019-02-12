package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

import java.util.List;
import java.util.Objects;

public final class EffectsFromSkills extends Skill {
    public EffectsFromSkills(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        targets.stream()
                .filter(Objects::nonNull)
                .forEach(target -> getAddedSkills().forEach(as ->
                        as.skill.getEffects(activeChar, target)));
    }
}