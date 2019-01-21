package l2trunk.gameserver;

import l2trunk.commons.threading.LoggingRejectedExecutionHandler;
import l2trunk.commons.threading.PriorityThreadFactory;
import l2trunk.commons.threading.RunnableStatsWrapper;

import java.util.concurrent.*;

public enum ThreadPoolManager {
    INSTANCE;

    private static final long MAX_DELAY = (long) 1000 * 60 * 60 * 24 * 365;
    private final ScheduledThreadPoolExecutor scheduledExecutor =
            new ScheduledThreadPoolExecutor(Config.SCHEDULED_THREAD_POOL_SIZE, new PriorityThreadFactory("ScheduledThreadPool", Thread.NORM_PRIORITY), new LoggingRejectedExecutionHandler());
    private ThreadPoolExecutor executor;
    private boolean _shutdown;

    public void init() {
        executor = new ThreadPoolExecutor(Config.EXECUTOR_THREAD_POOL_SIZE, Integer.MAX_VALUE, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new PriorityThreadFactory("ThreadPoolExecutor", Thread.NORM_PRIORITY), new LoggingRejectedExecutionHandler());

        //Очистка каждые 5 минут
        scheduleAtFixedRate(() -> {
            scheduledExecutor.purge();
            executor.purge();
        }, 300000L, 300000L);
    }

    private long validate(long delay) {
        return Math.max(0, Math.min(MAX_DELAY, delay));
    }

    public boolean isShutdown() {
        return _shutdown;
    }

    private Runnable wrap(Runnable r) {
        return Config.ENABLE_RUNNABLE_STATS ? RunnableStatsWrapper.wrap(r) : r;
    }

    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        return scheduledExecutor.schedule(wrap(r), validate(delay), TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay) {
        return scheduledExecutor.scheduleAtFixedRate(wrap(r), validate(initial), validate(delay), TimeUnit.MILLISECONDS);
    }

    public ScheduledFuture<?> scheduleAtFixedDelay(Runnable r, long initial, long delay) {
        return scheduledExecutor.scheduleWithFixedDelay(wrap(r), validate(initial), validate(delay), TimeUnit.MILLISECONDS);
    }

    public void execute(Runnable r) {
        executor.execute(wrap(r));
    }

    public void shutdown() throws InterruptedException {
        _shutdown = true;
        try {
            scheduledExecutor.shutdown();
            scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }

    public CharSequence getStats() {
        StringBuilder list = new StringBuilder();

        list.append("ScheduledThreadPool\n");
        list.append("=================================================\n");
        list.append("\tgetActiveCount: ...... ").append(scheduledExecutor.getActiveCount()).append("\n");
        list.append("\tgetCorePoolSize: ..... ").append(scheduledExecutor.getCorePoolSize()).append("\n");
        list.append("\tgetPoolSize: ......... ").append(scheduledExecutor.getPoolSize()).append("\n");
        list.append("\tgetLargestPoolSize: .. ").append(scheduledExecutor.getLargestPoolSize()).append("\n");
        list.append("\tgetMaximumPoolSize: .. ").append(scheduledExecutor.getMaximumPoolSize()).append("\n");
        list.append("\tgetCompletedTaskCount: ").append(scheduledExecutor.getCompletedTaskCount()).append("\n");
        list.append("\tgetQueuedTaskCount: .. ").append(scheduledExecutor.getQueue().size()).append("\n");
        list.append("\tgetTaskCount: ........ ").append(scheduledExecutor.getTaskCount()).append("\n");
        list.append("ThreadPoolExecutor\n");
        list.append("=================================================\n");
        list.append("\tgetActiveCount: ...... ").append(executor.getActiveCount()).append("\n");
        list.append("\tgetCorePoolSize: ..... ").append(executor.getCorePoolSize()).append("\n");
        list.append("\tgetPoolSize: ......... ").append(executor.getPoolSize()).append("\n");
        list.append("\tgetLargestPoolSize: .. ").append(executor.getLargestPoolSize()).append("\n");
        list.append("\tgetMaximumPoolSize: .. ").append(executor.getMaximumPoolSize()).append("\n");
        list.append("\tgetCompletedTaskCount: ").append(executor.getCompletedTaskCount()).append("\n");
        list.append("\tgetQueuedTaskCount: .. ").append(executor.getQueue().size()).append("\n");
        list.append("\tgetTaskCount: ........ ").append(executor.getTaskCount()).append("\n");

        return list;
    }
}
