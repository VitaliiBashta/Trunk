package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class FloatingGhost extends Fighter {
    public FloatingGhost(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isMoving)
            return false;

        randomWalk();
        return false;
    }

    @Override
    protected boolean randomWalk() {
        NpcInstance actor = getActor();
        Location sloc = actor.getSpawnedLoc();
        Location pos = Location.findPointToStay(actor, sloc, 50, 300);
        if (GeoEngine.canMoveToCoord(actor, pos)) {
            actor.setRunning();
            addTaskMove(pos, false);
        }
        return true;
    }
}