package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class ThroneofDestruction extends DefaultAI {
    private static final int DOOR = 12240031;
    private static final int TIAT_NPC_ID = 29163;
    private static final Location TIAT_LOC = new Location(-250403, 207273, -11952, 16384);
    private static final int[] checkNpcs = {18778, 18777};

    public ThroneofDestruction(NpcInstance actor) {
        super(actor);
        actor.block();
        actor.startDamageBlocked();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Reflection ref = actor.getReflection();
        if (checkAllDestroyed(actor.getNpcId())) {
            ref.openDoor(DOOR);
            ref.addSpawnWithoutRespawn(TIAT_NPC_ID, TIAT_LOC, 0);
        }
        super.onEvtDead(killer);
    }

    /**
     * Проверяет, уничтожены ли все Throne of Destruction Powerful Device в текущем измерении
     *
     * @return true если все уничтожены
     */
    private boolean checkAllDestroyed(int mobId) {
        for (NpcInstance npc : getActor().getReflection().getNpcs())
            if (ArrayUtils.contains(checkNpcs, npc.getNpcId()) && !npc.isDead())
                return false;
        return true;
    }
}