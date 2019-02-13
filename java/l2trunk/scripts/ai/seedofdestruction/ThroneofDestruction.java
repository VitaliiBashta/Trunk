package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class ThroneofDestruction extends DefaultAI {
    private static final int DOOR = 12240031;
    private static final int TIAT_NPC_ID = 29163;
    private static final Location TIAT_LOC = new Location(-250403, 207273, -11952, 16384);
    private static final List<Integer> checkNpcs = List.of(18778, 18777);

    public ThroneofDestruction(NpcInstance actor) {
        super(actor);
        actor.setBlock(true);
        actor.startDamageBlocked();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Reflection ref = actor.getReflection();
        if (checkAllDestroyed()) {
            ref.openDoor(DOOR);
            ref.addSpawnWithoutRespawn(TIAT_NPC_ID, TIAT_LOC);
        }
        super.onEvtDead(killer);
    }

    private boolean checkAllDestroyed() {
        return getActor().getReflection().getNpcs()
                .filter(npc -> checkNpcs.contains(npc.getNpcId()))
                .allMatch(Creature::isDead);
    }
}