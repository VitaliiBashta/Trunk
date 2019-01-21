package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerPercentCp extends Condition {
    private final double cp;

    public ConditionPlayerPercentCp(int cp) {
        this.cp = cp / 100.;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.getCurrentCpRatio() <= cp;
    }
}