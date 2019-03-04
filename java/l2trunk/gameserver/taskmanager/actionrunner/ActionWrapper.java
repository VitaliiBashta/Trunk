package l2trunk.gameserver.taskmanager.actionrunner;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

public abstract class ActionWrapper extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(ActionWrapper.class);

    private final String name;
    private Future<?> scheduledFuture;

    protected ActionWrapper(String name) {
        this.name = name;
    }

    public void schedule(long time) {
        scheduledFuture = ThreadPoolManager.INSTANCE.schedule(this, time);
    }

    public void cancel() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
            scheduledFuture = null;
        }
    }

    protected abstract void runImpl0();

    @Override
    public void runImpl() {
        try {
            runImpl0();
        } catch (Exception e) {
            _log.info("ActionWrapper: Exception: " + e + "; name: " + name, e);
        } finally {
            ActionRunner.INSTANCE.remove(name, this);

            scheduledFuture = null;
        }
    }

    public String getName() {
        return name;
    }
}
