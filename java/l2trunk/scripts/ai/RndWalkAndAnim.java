package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class RndWalkAndAnim extends DefaultAI {
    private static final int PET_WALK_RANGE = 100;

    public RndWalkAndAnim(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isMoving)
            return false;

        int val = Rnd.get(100);

        if (val < 10)
            randomWalk();
        else if (val < 20)
            actor.onRandomAnimation();

        return false;
    }

    @Override
    public boolean randomWalk() {
        NpcInstance actor = getActor();
        if (actor == null)
            return false;

        Location sloc = actor.getSpawnedLoc();

        int x = sloc.x + Rnd.get(2 * PET_WALK_RANGE) - PET_WALK_RANGE;
        int y = sloc.y + Rnd.get(2 * PET_WALK_RANGE) - PET_WALK_RANGE;
        int z = GeoEngine.getHeight(x, y, sloc.z, actor.getGeoIndex());

        actor.setRunning();
        actor.moveToLocation(Location.of(x, y, z), 0, true);

        return true;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}