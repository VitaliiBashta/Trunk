package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionTargetPercentMp extends Condition {
    private final double mp;

    public ConditionTargetPercentMp(int mp) {
        this.mp = mp / 100.;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.target != null && env.target.getCurrentMpRatio() <= mp;
    }
}