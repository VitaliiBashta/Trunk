package l2trunk.commons.threading;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum RunnableStatsManager {
    INSTANCE;
    private final Map<Class<?>, ClassStat> classStats = new HashMap<>();
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