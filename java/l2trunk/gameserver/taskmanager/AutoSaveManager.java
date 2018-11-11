package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.threading.SteppingRunnableQueueManager;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;

import java.util.concurrent.Future;

public class AutoSaveManager extends SteppingRunnableQueueManager {
    private static final AutoSaveManager _instance = new AutoSaveManager();

    private AutoSaveManager() {
        super(10000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 10000L, 10000L);
        //Очистка каждые 60 секунд
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                AutoSaveManager.this.purge();
            }

        }, 60000L, 60000L);
    }

    public static AutoSaveManager getInstance() {
        return _instance;
    }

    public Future<?> addAutoSaveTask(final Player player) {
        long delay = Rnd.get(180, 360) * 1000L;

        return scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (player == null || !player.isOnline())
                    return;

                player.store(true);
            }

        }, delay, delay);
    }
}