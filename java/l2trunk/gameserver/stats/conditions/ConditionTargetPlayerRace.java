package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetPlayerRace extends Condition {
    private final Race race;

    public ConditionTargetPlayerRace(String race) {
        this.race = Race.valueOf(race.toLowerCase());
    }

    @Override
    protected boolean testImpl(Env env) {
        Creature target = env.target;
        return target instanceof Player && race == ((Player) target).getRace();
    }
}