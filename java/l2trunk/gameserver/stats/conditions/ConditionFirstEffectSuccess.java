package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionFirstEffectSuccess extends Condition {
    private final boolean param;

    public ConditionFirstEffectSuccess(boolean param) {
        this.param = param;
    }

    @Override
    protected boolean testImpl(Env env) {
        return param == (env.value == Integer.MAX_VALUE);
    }
}