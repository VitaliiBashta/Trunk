package l2trunk.scripts.ai.primeval_isle;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;

public final class SprigantStun extends Fighter {

    private static final int SKILL = 5085;
    private long _waitTime;
    private static final int TICK_IN_MILISECONDS = 15000;

    public SprigantStun(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (System.currentTimeMillis() > _waitTime) {
            actor.doCast(SKILL, actor, false);
            _waitTime = System.currentTimeMillis() + TICK_IN_MILISECONDS;
        }
        actor.broadcastPacket(new SocialAction(actor.objectId(), 1));
        super.thinkActive();
        return true;
    }
}
