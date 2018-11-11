package l2trunk.scripts.ai.custom;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public class SSQAnakimMinion extends Fighter {
    private final int[] _enemies = {32717, 32716};

    public SSQAnakimMinion(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.getInstance().schedule(new Attack(), 3000);
    }

    public class Attack extends RunnableImpl {
        @Override
        public void runImpl() {
            if (getEnemy() != null)
                getActor().getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, getEnemy(), 10000000);
        }
    }

    private NpcInstance getEnemy() {
        List<NpcInstance> around = getActor().getAroundNpc(1000, 300);
        if (around != null && !around.isEmpty())
            for (NpcInstance npc : around)
                if (ArrayUtils.contains(_enemies, npc.getNpcId()))
                    return npc;
        return null;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}