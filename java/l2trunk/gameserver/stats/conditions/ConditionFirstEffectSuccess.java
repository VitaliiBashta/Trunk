package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public class ConditionFirstEffectSuccess extends Condition {
    private final boolean _param;

    public ConditionFirstEffectSuccess(boolean param) {
        _param = param;
    }

    @Override
    protected boolean testImpl(Env env) {
        return _param == (env.value == Integer.MAX_VALUE);
    }
}