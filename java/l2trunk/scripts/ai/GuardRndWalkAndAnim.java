package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Guard;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class GuardRndWalkAndAnim extends Guard {
    public GuardRndWalkAndAnim(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        if (super.thinkActive())
            return true;

        if (randomAnimation())
            return true;

        return randomWalk();

    }
}