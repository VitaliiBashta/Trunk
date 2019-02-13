package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;

public final class ConditionPlayerState extends Condition {
    private final CheckPlayerState _check;
    private final boolean _required;

    public ConditionPlayerState(CheckPlayerState check, boolean required) {
        _check = check;
        _required = required;
    }

    @Override
    protected boolean testImpl(Env env) {
        switch (_check) {
            case RESTING:
                if (env.character instanceof Player)
                    return ((Player) env.character).isSitting() == _required;
                return !_required;
            case MOVING:
                return env.character.isMoving == _required;
            case RUNNING:
                return (env.character.isMoving && env.character.isRunning()) == _required;
            case STANDING:
                if (env.character instanceof Player)
                    return ((Player) env.character).isSitting() != _required && env.character.isMoving != _required;
                return env.character.isMoving != _required;
            case FLYING:
                if (env.character instanceof Player)
                    return env.character.isFlying() == _required;
                return !_required;
            case FLYING_TRANSFORM:
                if (env.character instanceof Player)
                    return ((Player) env.character).isInFlyingTransform() == _required;
                return !_required;
        }
        return !_required;
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
