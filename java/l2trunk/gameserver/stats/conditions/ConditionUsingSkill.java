package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionUsingSkill extends Condition {
    private final int id;

    public ConditionUsingSkill(int id) {
        this.id = id;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.skill == null)
            return false;
        else
            return env.skill.id == id;
    }
}
