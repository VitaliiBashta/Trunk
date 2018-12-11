package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerOlympiad extends Condition {
    private final boolean _value;

    public ConditionPlayerOlympiad(boolean v) {
        _value = v;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.isInOlympiadMode() == _value || (env.character.isPlayable());
    }
}