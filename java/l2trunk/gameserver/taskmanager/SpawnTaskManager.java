package l2trunk.gameserver.taskmanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.List;

public enum SpawnTaskManager {
    INSTANCE;
    private final Object spawnTasks_lock = new Object();
    private SpawnTask[] _spawnTasks = new SpawnTask[500];
    private int _spawnTasksSize = 0;

    SpawnTaskManager() {
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new SpawnScheduler(), 2000, 2000);
    }

    public void addSpawnTask(NpcInstance actor, long interval) {
        removeObject(actor);
        addObject(new SpawnTask(actor, System.currentTimeMillis() + interval));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("============= SpawnTask Manager Report ============\n\r");
        sb.append("Tasks count: ").append(_spawnTasksSize).append("\n\r");
        sb.append("Tasks dump:\n\r");

        long current = System.currentTimeMillis();
        for (SpawnTask container : _spawnTasks) {
            sb.append("Class/Name: ").append(container.getClass().getSimpleName()).append('/').append(container.getActor());
            sb.append(" spawn timer: ").append(Util.formatTime((int) (container.endtime - current))).append("\n\r");
        }

        return sb.toString();
    }

    private void addObject(SpawnTask decay) {
        synchronized (spawnTasks_lock) {
            if (_spawnTasksSize >= _spawnTasks.length) {
                SpawnTask[] temp = new SpawnTask[_spawnTasks.length * 2];
                System.arraycopy(_spawnTasks, 0, temp, 0, _spawnTasksSize);
                _spawnTasks = temp;
            }

            _spawnTasks[_spawnTasksSize] = decay;
            _spawnTasksSize++;
        }
    }

    private void removeObject(NpcInstance actor) {
        synchronized (spawnTasks_lock) {
            if (_spawnTasksSize > 1) {
                int k = -1;
                for (int i = 0; i < _spawnTasksSize; i++)
                    if (_spawnTasks[i].getActor() == actor)
                        k = i;
                if (k > -1) {
                    _spawnTasks[k] = _spawnTasks[_spawnTasksSize - 1];
                    _spawnTasks[_spawnTasksSize - 1] = null;
                    _spawnTasksSize--;
                }
            } else if (_spawnTasksSize == 1 && _spawnTasks[0].getActor() == actor) {
                _spawnTasks[0] = null;
                _spawnTasksSize = 0;
            }
        }
    }

    public class SpawnScheduler extends RunnableImpl {
        @Override
        public void runImpl() {
            if (_spawnTasksSize > 0)
                try {
                    List<NpcInstance> tasks = new ArrayList<>();

                    synchronized (spawnTasks_lock) {
                        long current = System.currentTimeMillis();
                        int size = _spawnTasksSize;

                        for (int i = size - 1; i >= 0; i--)
                            try {
                                SpawnTask container = _spawnTasks[i];

                                if (container != null && container.endtime > 0 && current > container.endtime) {
                                    NpcInstance actor = container.getActor();
                                    if (actor != null && actor.getSpawn() != null)
                                        tasks.add(actor);

                                    container.endtime = -1;
                                }

                                if (container == null || container.getActor() == null || container.endtime < 0) {
                                    if (i == _spawnTasksSize - 1)
                                        _spawnTasks[i] = null;
                                    else {
                                        _spawnTasks[i] = _spawnTasks[_spawnTasksSize - 1];
                                        _spawnTasks[_spawnTasksSize - 1] = null;
                                    }

                                    if (_spawnTasksSize > 0)
                                        _spawnTasksSize--;
                                }
                            } catch (RuntimeException e) {
                                _log.error("c", e);
                            }
                    }

                    tasks.stream()
                            .filter(task -> task.getSpawn() != null)
                            .forEach(work -> {
                                Spawner spawn = work.getSpawn();
                                spawn.decreaseScheduledCount();
                                if (spawn.isDoRespawn())
                                    spawn.respawnNpc(work);
                            });
                } catch (RuntimeException e) {
                    _log.error("SpawnScheduler", e);
                }
        }
    }

    private class SpawnTask {
        private final NpcInstance npc;
        long endtime;

        SpawnTask(NpcInstance cha, long delay) {
            npc = cha;
            endtime = delay;
        }

        NpcInstance getActor() {
            return npc;
        }
    }
}