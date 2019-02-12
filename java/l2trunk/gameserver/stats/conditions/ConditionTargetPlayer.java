package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetPlayer extends Condition {
    private final boolean flag;

    public ConditionTargetPlayer(boolean flag) {
        this.flag = flag;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        return target != null && target instanceof Player == flag;
    }
}
