package l2trunk.commons.threading;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Mobius
 * @version $Revision: 1.0 $
 */
public final class RunnableStatsManager {
    /**
     * Field _instance.
     */
    private static final RunnableStatsManager _instance = new RunnableStatsManager();
    /**
     * Field classStats.
     */
    private final Map<Class<?>, ClassStat> classStats = new HashMap<>();
    /**
     * Field lock.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Method getInstance.
     *
     * @return RunnableStatsManager
     */
    public static RunnableStatsManager getInstance() {
        return _instance;
    }

    /**
     * Method handleStats.
     *
     * @param cl      Class<?>
     * @param runTime long
     */
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

    /**
     * Method getSortedClassStats.
     *
     * @return List<ClassStat>
     */
    private List<ClassStat> getSortedClassStats() {
        List<ClassStat> result;
        try {
            lock.lock();
            result = Arrays.asList(classStats.values().toArray(new ClassStat[classStats.size()]));
        } finally {
            lock.unlock();
        }
        result.sort((c1, c2) -> Long.compare(c2.maxTime, c1.maxTime));
        return result;
    }

    /**
     * Method getStats.
     *
     * @return CharSequence
     */
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

    /**
     * @author Mobius
     */
    private class ClassStat {
        /**
         * Field clazz.
         */
        final Class<?> clazz;
        /**
         * Field runCount.
         */
        long runCount = 0;
        /**
         * Field runTime.
         */
        long runTime = 0;
        /**
         * Field minTime.
         */
        long minTime = Long.MAX_VALUE;
        /**
         * Field maxTime.
         */
        long maxTime = Long.MIN_VALUE;

        /**
         * Constructor for ClassStat.
         *
         * @param cl Class<?>
         */
        ClassStat(Class<?> cl) {
            clazz = cl;
            classStats.put(cl, this);
        }
    }
}