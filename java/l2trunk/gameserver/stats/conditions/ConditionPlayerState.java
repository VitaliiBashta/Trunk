package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerState extends Condition {
    private final CheckPlayerState check;
    private final boolean required;

    public ConditionPlayerState(CheckPlayerState check, boolean required) {
        this.check = check;
        this.required = required;
    }

    @Override
    protected boolean testImpl(Env env) {
        switch (check) {
            case RESTING:
                if (env.character instanceof Player)
                    return ((Player) env.character).isSitting() == required;
                return !required;
            case MOVING:
                return env.character.isMoving == required;
            case RUNNING:
                return (env.character.isMoving && env.character.isRunning()) == required;
            case STANDING:
                if (env.character instanceof Player)
                    return ((Player) env.character).isSitting() != required && env.character.isMoving != required;
                return env.character.isMoving != required;
            case FLYING:
                if (env.character instanceof Player)
                    return env.character.isFlying() == required;
                return !required;
            case FLYING_TRANSFORM:
                if (env.character instanceof Player)
                    return ((Player) env.character).isInFlyingTransform() == required;
                return !required;
        }
        return !required;
    }

    public enum CheckPlayerState {
        RESTING,
        MOVING,
        RUNNING,
        STANDING,
        FLYING,
        FLYING_TRANSFORM
    }
}
