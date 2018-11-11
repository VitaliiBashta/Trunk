package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;

public class FeralHound extends Fighter {
    public FeralHound(NpcInstance actor) {
        super(actor);
        actor.setIsInvul(true);
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}