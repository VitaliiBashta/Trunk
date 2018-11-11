package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.stats.Env;

import java.util.HashSet;
import java.util.Set;

public class ConditionTargetForbiddenClassId extends Condition {
    private final Set<Integer> _classIds = new HashSet<>();

    public ConditionTargetForbiddenClassId(String[] ids) {
        for (String id : ids)
            _classIds.add(Integer.parseInt(id));
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        if (!target.isPlayable())
            return false;
        return !target.isPlayer() || !_classIds.contains(target.getPlayer().getActiveClassId());
    }
}