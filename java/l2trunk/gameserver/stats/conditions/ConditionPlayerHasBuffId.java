package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.stats.Env;


public final class ConditionPlayerHasBuffId extends Condition {
    private final int id;
    private final int level;

    public ConditionPlayerHasBuffId(int id, int level) {
        this.id = id;
        this.level = level;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature character = env.character;
        if (character == null)
            return false;
        return character.getEffectList().getEffectsBySkillId(id)
                .anyMatch(e -> e.skill.level >= level);
    }
}