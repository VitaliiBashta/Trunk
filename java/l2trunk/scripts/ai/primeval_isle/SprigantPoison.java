package l2trunk.scripts.ai.primeval_isle;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.tables.SkillTable;

public final class SprigantPoison extends Fighter {

    private long _waitTime;

    private static final int TICK_IN_MILISECONDS = 15000;

    public SprigantPoison(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (System.currentTimeMillis() > _waitTime) {
            actor.doCast(5086, actor, false);
            _waitTime = System.currentTimeMillis() + TICK_IN_MILISECONDS;
        }
        actor.broadcastPacket(new SocialAction(actor.getObjectId(), 1));
        super.thinkActive();
        return true;
    }
}
