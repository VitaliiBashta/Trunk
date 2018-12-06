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

        for (NpcInstance npc : World.getAroundNpc(minion))
            if (npc.getNpcId() == Kama63Minion.BOSS_ID)
                return npc;
        return null;
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
        NpcInstance _minion = null;
        NpcInstance _master = null;

        DieScheduleTimerTask(NpcInstance minion, NpcInstance master) {
            _minion = minion;
            _master = master;
        }

        @Override
        public void runImpl() {
            if (_master != null && _minion != null && !_master.isDead() && !_minion.isDead())
                _master.setCurrentHp(_master.getCurrentHp() + _minion.getCurrentHp() * 5, false);
            Functions.npcSayCustomMessage(_minion, "Kama63Minion");
            _minion.doDie(_minion);
        }
    }
}