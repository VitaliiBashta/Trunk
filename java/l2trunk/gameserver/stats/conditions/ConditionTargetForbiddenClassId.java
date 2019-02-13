package l2trunk.gameserver.stats.conditions;

import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ConditionTargetForbiddenClassId extends Condition {
    private final Set<Integer> classIds;

    public ConditionTargetForbiddenClassId(String ids) {
        classIds = Stream.of(ids.split(";"))
                .map(NumberUtils::toInt)
                .collect(Collectors.toSet());
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        if (target instanceof Player)
            return !classIds.contains(((Player) target).getActiveClassId());
        else
            return true;
    }
}