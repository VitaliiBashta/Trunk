package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

public abstract class ConditionInventory extends Condition {
    final int slot;

    ConditionInventory(int slot) {
        this.slot = slot;
    }

    @Override
    protected abstract boolean testImpl(Env env);
}