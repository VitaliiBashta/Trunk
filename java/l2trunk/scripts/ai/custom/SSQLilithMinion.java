package l2trunk.scripts.ai.custom;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class SSQLilithMinion extends Fighter {
    private final int[] _enemies = {32719, 32720, 32721};

    public SSQLilithMinion(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.INSTANCE().schedule(new Attack(), 3000);
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
    public boolean randomWalk() {
        return false;
    }
}