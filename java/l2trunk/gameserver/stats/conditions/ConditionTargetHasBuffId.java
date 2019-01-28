package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetHasBuffId extends Condition {
    private final int id;
    private final int level;

    public ConditionTargetHasBuffId(int id, int level) {
        this.id = id;
        this.level = level;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        if (target == null)
            return false;
        return target.getEffectList().getEffectsBySkillId(id)
                .anyMatch(e -> e.getSkill().level >= level);
    }
}
