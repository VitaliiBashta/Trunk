package l2trunk.scripts.ai.other.PailakaDevilsLegacy;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;

/**
 * - AI мобов Followers Lematan, миньёны-лекари Боса Lematan в пайлаке 61-67.
 * - Не умеют ходить, лечат Боса.
 */
public final class FollowersLematan extends Fighter {
    private static final int LEMATAN = 18633;

    public FollowersLematan(NpcInstance actor) {
        super(actor);
        startSkillTimer();
    }

    private void findBoss() {
        NpcInstance minion = getActor();
        if (minion == null)
            return;
        World.getAroundNpc(minion, 1000, 1000)
                .filter(target -> target.getNpcId() == LEMATAN && target.getCurrentHpPercents() < 65)
                .forEach(target -> minion.doCast(5712 , target, true));

    }

    private void startSkillTimer() {
        if (getActor() != null)
            ThreadPoolManager.INSTANCE.schedule(() -> {
                findBoss();
                startSkillTimer();
            },  20000);
    }


    @Override
    public boolean randomWalk() {
        return false;
    }
}