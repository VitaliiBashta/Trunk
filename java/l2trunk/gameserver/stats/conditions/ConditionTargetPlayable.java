package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetPlayable extends Condition {
    private final boolean flag;

    public ConditionTargetPlayable(boolean flag) {
        this.flag = flag;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        return target != null && target instanceof Playable == flag;
    }
}
