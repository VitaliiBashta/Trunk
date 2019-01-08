package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class NaiaLock extends Fighter {
    private static boolean attacked = false;
    private static boolean entranceActive = false;

    public NaiaLock(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    public static boolean isEntranceActive() {
        return entranceActive;
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        entranceActive = true;
        Functions.npcShout(actor, "The lock has been removed from the Controller device");
        super.onEvtDead(killer);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        entranceActive = false;
        Functions.npcShout(getActor(), "The lock has been put on the Controller device");
    }

    @Override
    public boolean checkAggression(Creature target, boolean avoidAttack) {
        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (!attacked) {
            for (int i = 0; i < 4; i++) {
                new SimpleSpawner(18493)
                        .setLoc(Location.findPointToStay(actor, 150, 250))
                        .setReflection(actor.getReflection())
                        .stopRespawn()
                        .doSpawn(true);
            }
            attacked = true;
        }
    }
}