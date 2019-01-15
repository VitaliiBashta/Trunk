package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class PaganGuard extends Mystic {
    public PaganGuard(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public boolean checkTarget(Creature target, int range) {
        NpcInstance actor = getActor();
        if (target != null && !actor.isInRange(target, actor.getAggroRange())) {
            actor.getAggroList().remove(target, true);
            return false;
        }
        return super.checkTarget(target, range);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}