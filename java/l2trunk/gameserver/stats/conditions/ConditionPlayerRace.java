package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerRace extends Condition {
    private final Race race;

    public ConditionPlayerRace(String race) {
        this.race = Race.valueOf(race.toLowerCase());
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer())
            return false;
        return ((Player) env.character).getRace() == race;
    }
}