package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.stats.Env;

public final class ConditionUsingItemType extends Condition {
    private final long _mask;

    public ConditionUsingItemType(long mask) {
        _mask = mask;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!(env.character instanceof Playable))
            return false;
        return (_mask & ((Playable) env.character).getWearedMask()) != 0;
    }
}
