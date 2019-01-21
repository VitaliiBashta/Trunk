package l2trunk.commons.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Delayed;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class SteppingRunnableQueueManager implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(SteppingRunnableQueueManager.class);
    /**
     * Field tickPerStepInMillis.
     */
    private final long tickPerStepInMillis;
    /**
     * Field queue.
     */
    private final List<SteppingScheduledFuture<?>> queue = new CopyOnWriteArrayList<>();
    /**
     * Field isRunning.
     */
    private final AtomicBoolean isRunning = new AtomicBoolean();

    protected SteppingRunnableQueueManager(long tickPerStepInMillis) {
        this.tickPerStepInMillis = tickPerStepInMillis;
    }


    /**
     * Method scheduleAtFixedRate.
     *
     * @param r       Runnable
     * @param initial long
     * @param delay   long
     * @return SteppingScheduledFuture<?>
     */
    public SteppingScheduledFuture<?> scheduleAtFixedRate(Runnable r, long initial, long delay) {
        SteppingScheduledFuture<?> sr;
        long initialStepping = getStepping(initial);
        long stepping = getStepping(delay);
        queue.add(sr = new SteppingScheduledFuture<Boolean>(r, initialStepping, stepping, true));
        return sr;
    }

    /**
     * Method getStepping.
     *
     * @param delay long
     * @return long
     */
    private long getStepping(long delay) {
        delay = Math.max(0, delay);
        return (delay % tickPerStepInMillis) > (tickPerStepInMillis / 2) ? (delay / tickPerStepInMillis) + 1 : delay < tickPerStepInMillis ? 1 : delay / tickPerStepInMillis;
    }

    @Override
    public void run() {
        if (!isRunning.compareAndSet(false, true)) {
            _log.warn("Slow running queue, managed by " + this + ", queue size : " + queue.size() + "!");
            return;
        }
        try {
            if (queue.isEmpty()) {
                return;
            }
            for (SteppingScheduledFuture<?> sr : queue) {
                if (!sr.isDone()) {
                    sr.run();
                }
            }
        } finally {
            isRunning.set(false);
        }
    }

    public void purge() {
        final List<SteppingScheduledFuture<?>> purge = queue.stream()
                .filter(Objects::nonNull)
                .filter(SteppingScheduledFuture::isDone)
                .collect(Collectors.toList());
        queue.removeAll(purge);
    }

    public CharSequence getStats() {
        StringBuilder list = new StringBuilder();
        Map<String, Long> stats = new TreeMap<>();
        int total = 0;
        int done = 0;
        for (SteppingScheduledFuture<?> sr : queue) {
            if (sr.isDone()) {
                done++;
                continue;
            }
            total++;
            Long count = stats.get(sr.r.getClass().getName());
            if (count == null) {
                stats.put(sr.r.getClass().getName(), count = 1L);
            } else {
                count++;
            }
        }
        for (Map.Entry<String, Long> e : stats.entrySet()) {
            list.append('\t').append(e.getKey()).append(" : ").append(e.getValue().longValue()).append('\n');
        }
        list.append("Scheduled: ....... ").append(total).append('\n');
        list.append("Done/Cancelled: .. ").append(done).append('\n');
        return list;
    }

    public class SteppingScheduledFuture<V> implements RunnableScheduledFuture<V> {
        final Runnable r;

        private final long stepping;
        private final boolean isPeriodic;
        private long step;
        private boolean isCancelled;

        /**
         * Constructor for SteppingScheduledFuture.
         *
         * @param r          Runnable
         * @param initial    long
         * @param stepping   long
         * @param isPeriodic boolean
         */
        SteppingScheduledFuture(Runnable r, long initial, long stepping, boolean isPeriodic) {
            this.r = r;
            this.step = initial;
            this.stepping = stepping;
            this.isPeriodic = isPeriodic;
        }

        /**
         * Method run.
         *
         * @see java.util.concurrent.RunnableFuture#run()
         */
        @Override
        public void run() {
            if (--step == 0) {
                try {
                    r.run();
                } catch (Exception e) {
                    _log.error("Exception in a Runnable execution:", e);
                } finally {
                    if (isPeriodic) {
                        step = stepping;
                    }
                }
            }
        }

        /**
         * Method isDone.
         *
         * @return boolean * @see java.util.concurrent.Future#isDone()
         */
        @Override
        public boolean isDone() {
            return isCancelled || (!isPeriodic && (step == 0));
        }

        /**
         * Method isCancelled.
         *
         * @return boolean * @see java.util.concurrent.Future#isCancelled()
         */
        @Override
        public boolean isCancelled() {
            return isCancelled;
        }

        /**
         * Method cancel.
         *
         * @param mayInterruptIfRunning boolean
         * @return boolean * @see java.util.concurrent.Future#cancel(boolean)
         */
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return isCancelled = true;
        }

        /**
         * Method get.
         *
         * @return V * @see java.util.concurrent.Future#get()
         */
        @Override
        public V get() {
            return null;
        }

        /**
         * Method get.
         *
         * @param timeout long
         * @param unit    TimeUnit
         * @return V * @see java.util.concurrent.Future#get(long, TimeUnit)
         */
        @Override
        public V get(long timeout, TimeUnit unit) {
            return null;
        }

        /**
         * Method getDelay.
         *
         * @param unit TimeUnit
         * @return long * @see java.util.concurrent.Delayed#getDelay(TimeUnit)
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(step * tickPerStepInMillis, TimeUnit.MILLISECONDS);
        }

        /**
         * Method compareTo.
         *
         * @param o Delayed
         * @return int
         */
        @Override
        public int compareTo(Delayed o) {
            return 0;
        }

        /**
         * Method isPeriodic.
         *
         * @return boolean * @see java.util.concurrent.RunnableScheduledFuture#isPeriodic()
         */
        @Override
        public boolean isPeriodic() {
            return isPeriodic;
        }
    }
}