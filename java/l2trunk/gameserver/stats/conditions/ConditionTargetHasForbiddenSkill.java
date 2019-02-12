package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetHasForbiddenSkill extends Condition {
    private final int skillId;

    public ConditionTargetHasForbiddenSkill(int skillId) {
        this.skillId = skillId;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        if (target instanceof Playable) {
            return !(target.getSkillLevel(skillId) > 0);
        }
        return false;
    }
}
