package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerMaxLevel extends Condition {
    private final int level;

    public ConditionPlayerMaxLevel(int level) {
        this.level = level;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.getLevel() <= level;
    }
}