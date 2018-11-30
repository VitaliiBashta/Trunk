package l2trunk.scripts.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Furance extends DefaultAI {
    public Furance(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        if (Rnd.chance(50))
            actor.setNpcState(1);
        ThreadPoolManager.INSTANCE().scheduleAtFixedRate(new Switch(), 5 * 60 * 1000L, 5 * 60 * 1000L);
    }

    public class Switch extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor.getNpcState() == 1)
                actor.setNpcState(2);
            else
                actor.setNpcState(1);
        }
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    public boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}