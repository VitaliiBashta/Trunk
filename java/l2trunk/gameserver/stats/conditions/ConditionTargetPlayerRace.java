package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.stats.Env;

public class ConditionTargetPlayerRace extends Condition {
    private final Race _race;

    public ConditionTargetPlayerRace(String race) {
        _race = Race.valueOf(race.toLowerCase());
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        return target != null && target.isPlayer() && _race == ((Player) target).getRace();
    }
}