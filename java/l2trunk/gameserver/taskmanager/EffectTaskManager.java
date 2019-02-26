package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.SteppingRunnableQueueManager;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

public final class EffectTaskManager extends SteppingRunnableQueueManager {
    private final static int TICK = 250;

    private final static List<EffectTaskManager> MANAGERS = new ArrayList<>();
    private static int randomizer = 0;

    static {
        for (int i = 0; i < Config.EFFECT_TASK_MANAGER_COUNT; i++)
            MANAGERS.add(new EffectTaskManager());
    }

    private EffectTaskManager() {
        super(TICK);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(this, Rnd.get(TICK), TICK);

        //Очистка каждые 30 секунд
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(EffectTaskManager.this::purge, 30000L, 30000L);
    }

    public static EffectTaskManager getInstance() {
        return MANAGERS.get(randomizer++ & MANAGERS.size() - 1);
    }

}