package l2f.gameserver.taskmanager;

import javolution.util.FastMap;
import l2f.gameserver.ThreadPoolManager;
import l2f.gameserver.model.Creature;
import l2f.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Clase muy eficiente para manejar los decays de todos los npcs. Basado en el de l2j
 *
 * @author GipsyGrierosu Andrei
 */
public class DecayTaskManager {
    protected static final Logger _log = LoggerFactory.getLogger(NpcInstance.class);

    protected final Map<Creature, Long> _decayTasks = new FastMap<Creature, Long>().setShared(true);

    protected DecayTaskManager() {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new DecayScheduler(), 10000, 5000);
    }

    public static DecayTaskManager getInstance() {
        return SingletonHolder._instance;
    }

    public void addDecayTask(Creature actor, long interval) {
        _decayTasks.put(actor, System.currentTimeMillis() + interval);
    }

    public void cancelDecayTask(Creature actor) {
        _decayTasks.remove(actor);
    }

    private static class SingletonHolder {
        protected static final DecayTaskManager _instance = new DecayTaskManager();
    }

    protected class DecayScheduler implements Runnable {
        @Override
        public void run() {
            final long current = System.currentTimeMillis();
            try {
                final Iterator<Entry<Creature, Long>> it = _decayTasks.entrySet().iterator();
                Entry<Creature, Long> e;
                Creature actor;
                Long next;
                while (it.hasNext()) {
                    e = it.next();
                    actor = e.getKey();
                    next = e.getValue();
                    if (actor == null || next == null)
                        continue;

                    if (current > next) {
                        actor.doDecay();
                        it.remove();
                    }
                }
            } catch (Exception e) {
                // TODO: Find out the reason for exception. Unless caught here, mob decay would stop.
                _log.warn("Error in DecayScheduler: " + e.getMessage(), e);
            }
        }
    }
}
