package l2trunk.commons.threading;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum RunnableStatsManager {
    INSTANCE;
    /**
     * Field classStats.
     */
    private final Map<Class<?>, ClassStat> classStats = new HashMap<>();
    /**
     * Field lock.
     */
    private final Lock lock = new ReentrantLock();

    public void handleStats(Class<?> cl, long runTime) {
        try {
            lock.lock();
            ClassStat stat = classStats.get(cl);
            if (stat == null) {
                stat = new ClassStat(cl);
            }
            stat.runCount++;
            stat.runTime += runTime;
            if (stat.minTime > runTime) {
                stat.minTime = runTime;
            }
            if (stat.maxTime < runTime) {
                stat.maxTime = runTime;
            }
        } finally {
            lock.unlock();
        }
    }

    private List<ClassStat> getSortedClassStats() {
        List<ClassStat> result;
        try {
            lock.lock();
            result = new ArrayList<>(classStats.values());
        } finally {
            lock.unlock();
        }
        result.sort((c1, c2) -> Long.compare(c2.maxTime, c1.maxTime));
        return result;
    }

    public CharSequence getStats() {
        StringBuilder list = new StringBuilder();
        List<ClassStat> stats = getSortedClassStats();
        for (ClassStat stat : stats) {
            list.append(stat.clazz.getName()).append(":\n");
            list.append("\tRun: ............ ").append(stat.runCount).append('\n');
            list.append("\tTime: ........... ").append(stat.runTime).append('\n');
            list.append("\tMin: ............ ").append(stat.minTime).append('\n');
            list.append("\tMax: ............ ").append(stat.maxTime).append('\n');
            list.append("\tAverage: ........ ").append(stat.runTime / stat.runCount).append('\n');
        }
        return list;
    }

    private class ClassStat {
        final Class<?> clazz;
        long runCount = 0;
        long runTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;

        ClassStat(Class<?> cl) {
            clazz = cl;
            classStats.put(cl, this);
        }
    }
}