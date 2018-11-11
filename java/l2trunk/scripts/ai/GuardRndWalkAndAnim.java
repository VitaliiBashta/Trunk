package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Guard;
import l2trunk.gameserver.model.instances.NpcInstance;

public class GuardRndWalkAndAnim extends Guard {
    public GuardRndWalkAndAnim(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        if (super.thinkActive())
            return true;

        if (randomAnimation())
            return true;

        if (randomWalk())
            return true;

        return false;
    }
}