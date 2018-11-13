package l2trunk.scripts.ai.custom;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class MutantChest extends Fighter {
    public MutantChest(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        if (Rnd.chance(30))
            Functions.npcSay(actor, "Enemies! Enemies everywhere! Everything here, the enemies here!");

        actor.deleteMe();
    }
}