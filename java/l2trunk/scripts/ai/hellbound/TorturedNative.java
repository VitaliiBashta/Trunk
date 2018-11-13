package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class TorturedNative extends Fighter {
    public TorturedNative(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        if (Rnd.chance(1))
            if (Rnd.chance(10))
                Functions.npcSay(actor, "Eeeek... I feel sick... yow...!");
            else
                Functions.npcSay(actor, "It... will... kill... everyone...!");

        return super.thinkActive();
    }
}