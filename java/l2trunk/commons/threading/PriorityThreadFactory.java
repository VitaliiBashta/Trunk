package l2trunk.commons.threading;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public final class PriorityThreadFactory implements ThreadFactory {
    private final int prio;
    private final String name;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final ThreadGroup threadGroup;

    public PriorityThreadFactory(String name, int prio) {
        this.prio = prio;
        this.name = name;
        threadGroup = new ThreadGroup(this.name);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(threadGroup, r);
        t.setName(name + "-" + threadNumber.getAndIncrement());
        t.setPriority(prio);
        return t;
    }

}