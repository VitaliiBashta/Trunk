package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.stats.Env;

public final class ConditionTargetCastleDoor extends Condition {
    private final boolean isCastleDoor;

    public ConditionTargetCastleDoor(boolean isCastleDoor) {
        this.isCastleDoor = isCastleDoor;
    }

    @Override
    protected boolean testImpl(Env env) {
        return env.target instanceof DoorInstance == isCastleDoor;
    }
}
