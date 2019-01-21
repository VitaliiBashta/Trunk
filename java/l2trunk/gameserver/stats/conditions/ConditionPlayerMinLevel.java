package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerMinLevel extends Condition {
    private final int level;

    public ConditionPlayerMinLevel(int level) {
        this.level = level;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.getLevel() >= level;
    }
}