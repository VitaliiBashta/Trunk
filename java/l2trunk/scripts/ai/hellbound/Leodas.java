package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ReflectionUtils;

public class Leodas extends Fighter {
    public Leodas(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        ReflectionUtils.getDoor(19250003).openMe();
        ReflectionUtils.getDoor(19250004).openMe();
        ThreadPoolManager.getInstance().schedule(new CloseDoor(), 60 * 1000L);
        super.onEvtDead(killer);
    }

    private class CloseDoor extends RunnableImpl {
        @Override
        public void runImpl() {
            ReflectionUtils.getDoor(19250003).closeMe();
            ReflectionUtils.getDoor(19250004).closeMe();
        }
    }
}