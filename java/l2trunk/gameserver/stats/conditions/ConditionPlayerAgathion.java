package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerAgathion extends Condition {
    private final int _agathionId;

    public ConditionPlayerAgathion(int agathionId) {
        _agathionId = agathionId;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character instanceof Player) {
            if (((Player) env.character).getAgathionId() > 0 && _agathionId == -1)
                return true;
            return ((Player) env.character).getAgathionId() == _agathionId;
        } else {
            return false;
        }
    }
}