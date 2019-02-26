package l2trunk.gameserver.taskmanager.actionrunner.tasks;

import l2trunk.gameserver.taskmanager.actionrunner.ActionRunner;
import l2trunk.gameserver.taskmanager.actionrunner.ActionWrapper;

public abstract class AutomaticTask extends ActionWrapper {
    private static final String TASKS = "automatic_tasks";

    AutomaticTask() {
        super(TASKS);
    }

    protected abstract void doTask();

    public final long reCalcTime() {
        return System.currentTimeMillis() + 600_000L;
    }

    @Override
    public void runImpl0() {
        try {
            doTask();
        } finally {
            ActionRunner.INSTANCE.register(reCalcTime(), this);
        }
    }
}
