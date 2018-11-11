package l2trunk.scripts.ai.other.PailakaDevilsLegacy;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

/**
 * - AI мобов Followers Lematan, миньёны-лекари Боса Lematan в пайлаке 61-67.
 * - Не умеют ходить, лечат Боса.
 */
public class FollowersLematan extends Fighter {
    private static final int LEMATAN = 18633;

    public FollowersLematan(NpcInstance actor) {
        super(actor);
        startSkillTimer();
    }

    private void findBoss() {
        NpcInstance minion = getActor();
        if (minion == null)
            return;

        for (NpcInstance target : World.getAroundNpc(minion, 1000, 1000)) {
            if (target.getNpcId() == LEMATAN && target.getCurrentHpPercents() < 65)
                minion.doCast(SkillTable.getInstance().getInfo(5712, 1), target, true);
        }
        return;
    }

    private void startSkillTimer() {
        if (getActor() != null)
            ScheduleTimerTask(20000);
    }

    private void ScheduleTimerTask(long time) {
        ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                findBoss();
                startSkillTimer();
            }
        }, time);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        // stop timers if any
        super.onEvtDead(killer);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}