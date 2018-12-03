package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.SteppingRunnableQueueManager;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

public final class AiTaskManager extends SteppingRunnableQueueManager {
    private final static long TICK = 250L;

    private final static List<AiTaskManager> _instances = new ArrayList<>(Config.AI_TASK_MANAGER_COUNT);
    private static int randomizer = 0;

    static {
        for (int i = 0; i < Config.AI_TASK_MANAGER_COUNT; i++)
            _instances.add(new AiTaskManager());
    }

    private AiTaskManager() {
        super(TICK);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(this, Rnd.get(TICK), TICK);
        //Очистка каждую минуту
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(AiTaskManager.this::purge, 60000L, 60000L);
    }

    public static AiTaskManager getInstance() {
        return _instances.get(randomizer++ & _instances.size() - 1);
    }

    public CharSequence getStats(int num) {
        return _instances.get(num).getStats();
    }
}
