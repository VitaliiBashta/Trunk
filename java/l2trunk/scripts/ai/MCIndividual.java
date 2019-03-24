package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;


public final class MCIndividual extends DefaultAI {
    public MCIndividual(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        if (actor == null)
            return;

        ThreadPoolManager.INSTANCE.schedule(new ScheduleSocial(), 1000);
        super.onEvtSpawn();
    }

    private class ScheduleSocial implements Runnable {
        @Override
        public void run() {
//            NpcInstance actor = getActor();
            actor.broadcastPacket(new SocialAction(actor.objectId(), 1));
        }
    }
}