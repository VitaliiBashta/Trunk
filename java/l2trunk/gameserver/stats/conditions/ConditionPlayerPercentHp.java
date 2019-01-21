package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerPercentHp extends Condition {
    private final double hp;

    public ConditionPlayerPercentHp(int hp) {
        this.hp = hp / 100.;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.getCurrentHpRatio() <= hp;
    }
}