package l2trunk.scripts.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

public final class FollowNpc extends DefaultAI {
    private static final Logger LOG = LoggerFactory.getLogger(FollowNpc.class);
    private boolean _thinking = false;
    private ScheduledFuture<?> _followTask;

    private FollowNpc(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean randomWalk() {
        return getActor() instanceof MonsterInstance;
    }

    @Override
    public void onEvtThink() {
        NpcInstance actor = getActor();
        if (_thinking || actor.isActionsDisabled() || actor.isAfraid() || actor.isDead() || actor.isMovementDisabled())
            return;

        _thinking = true;
        try {
            if (!Config.BLOCK_ACTIVE_TASKS && (getIntention() == CtrlIntention.AI_INTENTION_ACTIVE || getIntention() == CtrlIntention.AI_INTENTION_IDLE))
                thinkActive();
            else if (getIntention() == CtrlIntention.AI_INTENTION_FOLLOW)
                thinkFollow();
        } catch (RuntimeException e) {
            LOG.error("Error while thinking on FollowNpc", e);
        } finally {
            _thinking = false;
        }
    }

    private void thinkFollow() {
        NpcInstance actor = getActor();

        Creature target = actor.getFollowTarget();

        //Находимся слишком далеко цели, либо цель не пригодна для следования, либо не можем перемещаться
        if (target == null || target.isAlikeDead() || actor.getDistance(target) > 4000 || actor.isMovementDisabled()) {
            clientActionFailed();
            return;
        }

        //Уже следуем за этой целью
        if (actor.isFollow && actor.getFollowTarget() == target) {
            clientActionFailed();
            return;
        }

        //Находимся достаточно близко
        if (actor.isInRange(target, Config.FOLLOW_RANGE + 20))
            clientActionFailed();

        if (_followTask != null) {
            _followTask.cancel(false);
            _followTask = null;
        }

        _followTask = ThreadPoolManager.getInstance().schedule(new ThinkFollow(), 250L);
    }

    protected class ThinkFollow extends RunnableImpl {
        NpcInstance getActor() {
            return FollowNpc.this.getActor();
        }

        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor == null)
                return;

            Creature target = actor.getFollowTarget();

            if (target == null || target.isAlikeDead() || actor.getDistance(target) > 4000) {
                setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                return;
            }

            if (!actor.isInRange(target, Config.FOLLOW_RANGE + 20) && (!actor.isFollow || actor.getFollowTarget() != target)) {
                Location loc = new Location(target.getX() + Rnd.get(-60, 60), target.getY() + Rnd.get(-60, 60), target.getZ());
                actor.followToCharacter(loc, target, Config.FOLLOW_RANGE, false);
            }
            _followTask = ThreadPoolManager.getInstance().schedule(this, 250L);
        }
    }
}