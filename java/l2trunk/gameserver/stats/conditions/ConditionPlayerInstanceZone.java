package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerInstanceZone extends Condition {
    private final int id;

    public ConditionPlayerInstanceZone(int id) {
        this.id = id;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.character.getReflection().getInstancedZoneId() == id;
    }
}