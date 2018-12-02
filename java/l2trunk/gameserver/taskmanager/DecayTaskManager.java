package l2trunk.gameserver.taskmanager;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public enum DecayTaskManager {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);

    private final Map<Creature, Long> decayTasks = new ConcurrentHashMap<>();

    DecayTaskManager() {
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new DecayScheduler(), 10000, 5000);
    }

    public void addDecayTask(Creature actor, long interval) {
        decayTasks.put(actor, System.currentTimeMillis() + interval);
    }

    public void cancelDecayTask(Creature actor) {
        decayTasks.remove(actor);
    }

    class DecayScheduler implements Runnable {
        @Override
        public void run() {
            final long current = System.currentTimeMillis();
            try {
                final Iterator<Entry<Creature, Long>> it = decayTasks.entrySet().iterator();
                Entry<Creature, Long> e;
                while (it.hasNext()) {
                    e = it.next();
                    if (current > e.getValue()) {
                        e.getKey().doDecay();
                        it.remove();
                    }
                }
            } catch (RuntimeException e) {
                _log.warn("Error in DecayScheduler: " + e.getMessage(), e);
            }
        }
    }
}
