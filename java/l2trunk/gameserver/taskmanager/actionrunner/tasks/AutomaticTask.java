package l2trunk.gameserver.taskmanager.actionrunner.tasks;

import l2trunk.gameserver.taskmanager.actionrunner.ActionRunner;
import l2trunk.gameserver.taskmanager.actionrunner.ActionWrapper;

public abstract class AutomaticTask extends ActionWrapper {
    private static final String TASKS = "automatic_tasks";

    AutomaticTask() {
        super(TASKS);
    }

    protected abstract void doTask();

    public abstract long reCalcTime(boolean start);

    @Override
    public void runImpl0() {
        try {
            doTask();
        } finally {
            ActionRunner.getInstance().register(reCalcTime(false), this);
        }
    }
}
