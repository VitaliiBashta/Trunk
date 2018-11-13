package l2trunk.scripts.ai.Zone.LairOfAntharas;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.HashMap;
import java.util.Map;


public final class BloodyKarik extends Fighter {

    private final int BLOODYKARIK = 22854;
    private final int BLOODYKARIK_COUNT = 5;
    private final int BKARIK_D_M_CHANCE = 5;
    private final Map<Integer,Integer> spawned_minion = new HashMap<>();

    public BloodyKarik(NpcInstance actor) {
        super(actor);
        spawned_minion.put(1, 1);
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);
        NpcInstance npc = getActor();
        if (Rnd.chance(BKARIK_D_M_CHANCE) && !spawned_minion.containsKey(npc.getObjectId())) {
            for (int x = 0; x < BLOODYKARIK_COUNT; x++) {
                NpcInstance mob = NpcHolder.getInstance().getTemplate(BLOODYKARIK).getNewInstance();
                mob.setSpawnedLoc(npc.getLoc());
                mob.setReflection(npc.getReflection());
                mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp(), true);
                mob.spawnMe(mob.getSpawnedLoc());
                mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer.getPlayer(), 1);
                spawned_minion.put(mob.getObjectId(), 1);
            }
        }
        spawned_minion.remove(npc.getObjectId());
    }
}
