package l2trunk.scripts.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.concurrent.ScheduledFuture;


/**
 * АИ для камалоки 63 уровня.
 * Каждые 30 секунд босс призывает миньона, который через 25 секунд совершает суицид и восстанавливает здоровье
 * боса.
 *
 * @author SYS
 */
public final class Kama63Minion extends Fighter {
    private static final int BOSS_ID = 18571;
    private static final int MINION_DIE_TIME = 25000;
    private long _wait_timeout = 0;
    private NpcInstance _boss;
    private boolean _spawned = false;
    private ScheduledFuture<?> _dieTask = null;

    public Kama63Minion(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        _boss = findBoss();
        super.onEvtSpawn();
    }

    @Override
    public boolean thinkActive() {
        if (_boss == null)
            _boss = findBoss();
        else if (!_spawned) {
            _spawned = true;
            Functions.npcSayCustomMessage(_boss, "Kama63Boss");
            NpcInstance minion = getActor();
            minion.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _boss.getAggroList().getRandomHated(), Rnd.get(1, 100));
            _dieTask = ThreadPoolManager.INSTANCE.schedule(new DieScheduleTimerTask(minion, _boss), MINION_DIE_TIME);
        }
        return super.thinkActive();
    }

    private NpcInstance findBoss() {
        // Ищем боса не чаще, чем раз в 15 секунд, если по каким-то причинам его нету
        if (System.currentTimeMillis() < _wait_timeout)
            return null;

        _wait_timeout = System.currentTimeMillis() + 15000;

        NpcInstance minion = getActor();
        if (minion == null)
            return null;

        return World.getAroundNpc(minion)
                .filter(npc -> npc.getNpcId() == BOSS_ID)
                .findFirst().orElse(null);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _spawned = false;
        if (_dieTask != null) {
            _dieTask.cancel(false);
            _dieTask = null;
        }
        super.onEvtDead(killer);
    }

    public class DieScheduleTimerTask extends RunnableImpl {
        NpcInstance minion;
        NpcInstance master;

        DieScheduleTimerTask(NpcInstance minion, NpcInstance master) {
            this.minion = minion;
            this.master = master;
        }

        @Override
        public void runImpl() {
            if (master != null && minion != null && !master.isDead() && !minion.isDead())
                master.setCurrentHp(master.getCurrentHp() + minion.getCurrentHp() * 5, false);
            Functions.npcSayCustomMessage(minion, "Kama63Minion");
            minion.doDie(minion);
        }
    }
}