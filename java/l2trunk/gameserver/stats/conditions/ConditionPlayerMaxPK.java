package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerMaxPK extends Condition {
    private final int pk;

    public ConditionPlayerMaxPK(int pk) {
        this.pk = pk;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character instanceof Player)
            return ((Player) env.character).getPkKills() <= pk;
        return false;
    }
}