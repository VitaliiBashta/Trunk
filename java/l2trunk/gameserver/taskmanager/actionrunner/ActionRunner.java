package l2trunk.gameserver.taskmanager.actionrunner;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.taskmanager.actionrunner.tasks.AutomaticTask;
import l2trunk.gameserver.taskmanager.actionrunner.tasks.DeleteExpiredMailTask;
import l2trunk.gameserver.taskmanager.actionrunner.tasks.DeleteExpiredVarsTask;
import l2trunk.gameserver.taskmanager.actionrunner.tasks.OlympiadSaveTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum  ActionRunner {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(ActionRunner.class);
//    private static final ActionRunner INSTANCE = new ActionRunner();
    private final Lock _lock = new ReentrantLock();
    private final Map<String, List<ActionWrapper>> _futures = new HashMap<>();

    ActionRunner() {
        if (Config.ENABLE_OLYMPIAD)
            register(new OlympiadSaveTask());
        register(new DeleteExpiredVarsTask());
        register(new DeleteExpiredMailTask());
    }

//    public static ActionRunner INSTANCE() {
//        return INSTANCE;
//    }

    private void register(AutomaticTask task) {
        register(task.reCalcTime(true), task);
    }

    public void register(long time, ActionWrapper wrapper) {
        if (time == 0) {
            LOG.info("Try register " + wrapper.getName() + " not defined time.");
            return;
        }

        if (time <= System.currentTimeMillis()) {
            ThreadPoolManager.INSTANCE.execute(wrapper);
            return;
        }

        addScheduled(wrapper.getName(), wrapper, time - System.currentTimeMillis());
    }

    private synchronized void addScheduled(String name, final ActionWrapper r, long diff) {
        List<ActionWrapper> wrapperList = _futures.computeIfAbsent(name.toLowerCase(), k -> new ArrayList<>());
        r.schedule(diff);
        wrapperList.add(r);
    }

    synchronized void remove(String name, ActionWrapper f) {
        final String lower = name.toLowerCase();
        List<ActionWrapper> wrapperList = _futures.get(lower);
        if (wrapperList == null)
            return;

        wrapperList.remove(f);

        if (wrapperList.isEmpty())
            _futures.remove(lower);
    }

    public void clear(String name) {
        _lock.lock();

        try {
            final String lower = name.toLowerCase();
            List<ActionWrapper> wrapperList = _futures.remove(lower);
            if (wrapperList == null)
                return;

            for (ActionWrapper f : wrapperList)
                f.cancel();

            wrapperList.clear();
        } finally {
            _lock.unlock();
        }
    }

    public synchronized void info() {
        _futures.forEach((k, v) -> LOG.info("Name: " + k + "; size: " + v.size()));
    }
}
