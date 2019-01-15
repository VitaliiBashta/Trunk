package l2trunk.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RunnableStatsWrapper implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(RunnableStatsWrapper.class);
    private final Runnable runnable;

    private RunnableStatsWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    public static Runnable wrap(Runnable runnable) {
        return new RunnableStatsWrapper(runnable);
    }

    private static void execute(Runnable runnable) {
        long begin = System.nanoTime();
        try {
            runnable.run();
            RunnableStatsManager.INSTANCE.handleStats(runnable.getClass(), System.nanoTime() - begin);
        } catch (RuntimeException e) {
            LOG.error("Exception in a Runnable execution:", e);
        }
    }

    @Override
    public void run() {
        execute(runnable);
    }
}