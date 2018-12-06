package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class GuardoftheGrave extends Fighter {
    private static final int DESPAWN_TIME = 2 * 45 * 1000;
    private static final int CHIEFTAINS_TREASURE_CHEST = 18816;

    private GuardoftheGrave(NpcInstance actor) {
        super(actor);
        actor.setInvul(true);
        actor.startImmobilized();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.INSTANCE.schedule(() -> {
            NpcInstance actor = getActor();
            spawnChest(actor);
            actor.deleteMe();
        }, DESPAWN_TIME + Rnd.get(1, 30));
    }

    @Override
    public boolean checkTarget(Creature target, int range) {
        NpcInstance actor = getActor();
        if (actor != null && target != null && !actor.isInRange(target, actor.getAggroRange())) {
            actor.getAggroList().remove(target, true);
            return false;
        }
        return super.checkTarget(target, range);
    }

    private void spawnChest(NpcInstance actor) {
        NpcInstance npc = NpcHolder.getTemplate(CHIEFTAINS_TREASURE_CHEST).getNewInstance();
        npc.setSpawnedLoc(actor.getLoc());
        npc.setFullHpMp();
        npc.spawnMe(npc.getSpawnedLoc());
    }

}