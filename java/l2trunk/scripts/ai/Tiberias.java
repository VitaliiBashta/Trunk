package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class Tiberias extends Fighter {
    public Tiberias(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        Functions.npcShoutCustomMessage(actor, "scripts.ai.Tiberias.kill");
        super.onEvtDead(killer);
    }
}