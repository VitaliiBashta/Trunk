package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.instancemanager.naia.NaiaCoreManager;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class NaiaCube extends DefaultAI {

    public NaiaCube(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.INSTANCE.schedule(new Despawn(getActor()), 300 * 1000L);
    }

    private class Despawn extends RunnableImpl {
        final NpcInstance _npc;

        private Despawn(NpcInstance npc) {
            _npc = npc;
        }

        @Override
        public void runImpl() {
            _npc.deleteMe();
            NaiaCoreManager.setZoneActive(false);
            NaiaCoreManager.setBossSpawned(false);
            ReflectionUtils.getDoor(20240001).openMe(); // Beleth Door
            ReflectionUtils.getDoor(18250025).openMe(); // Epidos Door
        }
    }
}