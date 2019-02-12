package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetMob extends Condition {
    private final boolean isMob;

    public ConditionTargetMob(boolean isMob) {
        this.isMob = isMob;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.target != null && env.target instanceof MonsterInstance == isMob;
    }
}
