package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public final class ConditionTargetNpcClass extends Condition {
    private final String npcClass;

    public ConditionTargetNpcClass(String name) {
            npcClass = name;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.target != null && env.target.getClass().getSimpleName().equalsIgnoreCase(npcClass);
    }
}
