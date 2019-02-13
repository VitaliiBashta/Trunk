package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerOlympiad extends Condition {
    private final boolean value;

    public ConditionPlayerOlympiad(boolean v) {
        value = v;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character instanceof Player) {
            Player player = (Player) env.character;

            return player.isInOlympiadMode() == value;
        }
        return false;
    }
}