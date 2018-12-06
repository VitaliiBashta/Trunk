package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Kama56Minion extends Fighter {
    public Kama56Minion(NpcInstance actor) {
        super(actor);
        actor.setInvul(true);
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
        if (aggro < 10000000)
            return;
        super.onEvtAggression(attacker, aggro);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }
}