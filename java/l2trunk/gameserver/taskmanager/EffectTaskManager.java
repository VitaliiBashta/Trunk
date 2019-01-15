package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.SteppingRunnableQueueManager;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;

public final class EffectTaskManager extends SteppingRunnableQueueManager {
    private final static int TICK = 250;

    private final static EffectTaskManager[] _instances = new EffectTaskManager[Config.EFFECT_TASK_MANAGER_COUNT];
    private static int randomizer = 0;

    static {
        for (int i = 0; i < _instances.length; i++)
            _instances[i] = new EffectTaskManager();
    }

    private EffectTaskManager() {
        super(TICK);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(this, Rnd.get(TICK), TICK);

        //Очистка каждые 30 секунд
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(EffectTaskManager.this::purge, 30000L, 30000L);
    }

    public static EffectTaskManager getInstance() {
        return _instances[randomizer++ & _instances.length - 1];
    }

    public CharSequence getStats(int num) {
        return _instances[num].getStats();
    }
}