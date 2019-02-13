package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerRiding extends Condition {
    private final CheckPlayerRiding _riding;

    public ConditionPlayerRiding(CheckPlayerRiding riding) {
        _riding = riding;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (env.character instanceof Player) {
            if (_riding == CheckPlayerRiding.STRIDER && ((Player) env.character).isRiding())
                return true;
            if (_riding == CheckPlayerRiding.WYVERN && env.character.isFlying())
                return true;
            return _riding == CheckPlayerRiding.NONE && !((Player) env.character).isRiding() && !env.character.isFlying();
        } else {
            return false;
        }
    }

    public enum CheckPlayerRiding {
        NONE,
        STRIDER,
        WYVERN
    }
}
