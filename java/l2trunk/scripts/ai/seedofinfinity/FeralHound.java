package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class FeralHound extends Fighter {
    public FeralHound(NpcInstance actor) {
        super(actor);
        actor.setInvul(true);
    }

    @Override
    public boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}