package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;


public final class KashasEye extends DefaultAI {
    public KashasEye(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);
        NpcInstance actor = getActor();
        if (actor != null && killer != null && actor != killer && Rnd.chance(35))
            actor.setDisplayId(Rnd.get(18812, 18814));
    }
}