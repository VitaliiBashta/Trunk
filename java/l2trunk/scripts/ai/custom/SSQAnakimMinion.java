package l2trunk.scripts.ai.custom;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class SSQAnakimMinion extends Fighter {
    private final List<Integer> enemies = List.of(32717, 32716);

    public SSQAnakimMinion(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.INSTANCE.schedule(new Attack(), 3000);
    }

    private NpcInstance getEnemy() {
        return getActor().getAroundNpc(1000, 300)
                .filter(npc -> enemies.contains(npc.getNpcId()))
                .findFirst().orElse(null);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    public class Attack extends RunnableImpl {
        @Override
        public void runImpl() {
            if (getEnemy() != null)
                getActor().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, getEnemy(), 10000000);
        }
    }
}