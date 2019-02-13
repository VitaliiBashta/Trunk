package l2trunk.gameserver.stats.conditions;

import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ConditionPlayerClassId extends Condition {
    private final List<Integer> classIds;

    public ConditionPlayerClassId(String ids) {
        classIds = Stream.of(ids.split(","))
                .map(NumberUtils::toInt)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!(env.character instanceof Player))
            return false;

        int playerClassId = ((Player) env.character).getActiveClassId();
        return classIds.contains(playerClassId);
    }
}