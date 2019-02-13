package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class MoSMonk extends Fighter {
    public MoSMonk(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onIntentionAttack(Creature target) {
        if (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE && Rnd.chance(20))
            Functions.npcSayCustomMessage(getActor(), "scripts.ai.MoSMonk.onIntentionAttack");
        super.onIntentionAttack(target);
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        if (target.getActiveWeaponInstance() == null)
            return false;
        return super.checkAggression(target, avoidAttack);
    }
}