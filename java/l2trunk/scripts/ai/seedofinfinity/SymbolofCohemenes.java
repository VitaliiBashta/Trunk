package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class SymbolofCohemenes extends DefaultAI {

    public SymbolofCohemenes(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}