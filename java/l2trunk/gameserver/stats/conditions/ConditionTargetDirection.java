package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.utils.PositionUtils;

public final class ConditionTargetDirection extends Condition {
    private final PositionUtils.TargetDirection direction;

    public ConditionTargetDirection(PositionUtils.TargetDirection direction) {
        this.direction = direction;
    }

    @Override
    protected boolean testImpl(Env env) {
        return PositionUtils.getDirectionTo(env.target, env.character) == direction;
    }
}
