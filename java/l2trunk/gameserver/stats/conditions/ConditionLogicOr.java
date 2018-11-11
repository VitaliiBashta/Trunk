package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.stats.Env;

import java.util.ArrayList;
import java.util.List;

public final class ConditionLogicOr extends Condition {
    public List<Condition> _conditions = new ArrayList<>();

    public void add(Condition condition) {
        if (condition == null)
            return;
        _conditions.add(condition);
    }

    @Override
    protected boolean testImpl(Env env) {
        for (Condition c : _conditions)
            if (c.test(env))
                return true;
        return false;
    }
}
