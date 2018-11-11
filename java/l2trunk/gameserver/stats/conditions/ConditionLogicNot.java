package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionLogicNot extends Condition {
    private final Condition _condition;

    public ConditionLogicNot(Condition condition) {
        _condition = condition;
    }

    @Override
    protected boolean testImpl(Env env) {
        return !_condition.test(env);
    }
}
