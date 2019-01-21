package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

import java.util.Objects;

public final class ConditionTargetActiveSkillId extends Condition {
    private final int skillId;

    public ConditionTargetActiveSkillId(int skillId) {
        this.skillId = skillId;
    }

    @Override
    public boolean testImpl(Env env) {
        return env.target.getAllSkills().stream()
                .filter(Objects::nonNull)
                .anyMatch(sk -> sk.getId() == skillId);
    }
}
