package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerPercentMp extends Condition {
    private final double mp;

    public ConditionPlayerPercentMp(int mp) {
        this.mp = mp / 100.;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.getCurrentMpRatio() <= mp;
    }
}