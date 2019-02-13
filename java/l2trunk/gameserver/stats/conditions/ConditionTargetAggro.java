package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetAggro extends Condition {
    private final boolean isAggro;

    public ConditionTargetAggro(boolean isAggro) {
        this.isAggro = isAggro;
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        if (target == null)
            return false;
        if (target instanceof MonsterInstance)
            return ((MonsterInstance) target).isAggressive() == isAggro;
        if (target instanceof Player)
            return ((Player)target).getKarma() > 0;
        return false;
    }
}
