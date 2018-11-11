package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public class TownGuard extends Fighter {
    public TownGuard(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        NpcInstance actor = getActor();
        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(50))
            Functions.npcSay(actor, "Invader!");
        super.onIntentionAttack(target);
    }
}