package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class GreaterEvil extends Fighter {
    private static final Location[] path = {
            Location.of(28448, 243816, -3696),
            Location.of(27624, 245256, -3696),
            Location.of(27528, 246808, -3656),
            Location.of(28296, 247912, -3248),
            Location.of(25880, 246184, -3176)};

    private int current_point = 0;

    public GreaterEvil(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 6000;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (defThink) {
            doTask();
            return true;
        }

        if (current_point >= path.length - 1) {
            actor.doDie(null);
            current_point = 0;
            return true;
        }
        actor.setRunning();
        addTaskMove(path[current_point], false);
        doTask();
        return false;
    }

    @Override
    public void onEvtArrived() {
        current_point++;
        super.onEvtArrived();
    }

    @Override
    public boolean maybeMoveToHome() {
        return false;
    }
}