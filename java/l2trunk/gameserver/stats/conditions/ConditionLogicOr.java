package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

import java.util.ArrayList;
import java.util.List;

public final class ConditionLogicOr extends Condition {
    public List<Condition> conditions = new ArrayList<>();

    public void add(Condition condition) {
        if (condition == null)
            return;
        conditions.add(condition);
    }

    @Override
    protected boolean testImpl(Env env) {
        return conditions.stream()
                .anyMatch(c -> c.test(env));
    }
}
