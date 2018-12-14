package l2trunk.scripts.ai.freya;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.concurrent.ScheduledFuture;

public final class IceKnightNormal extends Fighter {
    private boolean iced;
    private ScheduledFuture<?> task;

    public IceKnightNormal(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = 6000;
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        NpcInstance actor = getActor();
        iced = true;
        actor.setNpcState(1);
        actor.setBlock(true);
        aggroPlayers();

        task = ThreadPoolManager.INSTANCE.schedule(new ReleaseFromIce(), 6000L);
    }

    private void aggroPlayers() {
        Reflection r = getActor().getReflection();
        if (r != null && r.getPlayers() != null) {
            for (Player p : r.getPlayers()) {
                notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 300);
            }
        }
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (iced) {
            iced = false;
            if (task != null)
                task.cancel(false);
            actor.setBlock();
            actor.setNpcState(2);
        }
        super.onEvtAttacked(attacker, damage);
    }

    private class ReleaseFromIce extends RunnableImpl {
        @Override
        public void runImpl() {
            if (iced) {
                iced = false;
                getActor().setNpcState(2);
                getActor().setBlock();
                aggroPlayers(); // Additional aggro
            }
        }
    }

    @Override
    public void teleportHome() {
    }
}