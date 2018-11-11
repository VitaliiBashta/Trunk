package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public class ConditionUsingSkill extends Condition {
    private final int _id;

    public ConditionUsingSkill(int id) {
        _id = id;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.skill == null)
            return false;
        else
            return env.skill.getId() == _id;
    }
}
