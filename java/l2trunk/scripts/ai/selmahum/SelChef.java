package l2trunk.scripts.ai.selmahum;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class SelChef extends Fighter {
    private long wait_timeout = 0;

    public SelChef(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        getActor().getMinionList().spawnMinions();
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
        if (System.currentTimeMillis() > wait_timeout) {
            wait_timeout = System.currentTimeMillis() + 2000;
            actor.setWalking();
            Location targetLoc = findFirePlace(actor);
            addTaskMove(targetLoc, true);
            doTask();
            return true;
        }
        return false;
    }

    private Location findFirePlace(NpcInstance actor) {
        return actor.getAroundNpc(3000, 600)
                .filter(npc -> npc.getNpcId() == 18927)
                .filter(npc -> GeoEngine.canSeeTarget(actor, npc, false))
                .map(GameObject::getLoc)
                .findAny().orElse(Location.findPointToStay(actor, 1000, 1500));
    }

    @Override
    public boolean maybeMoveToHome() {
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}