package l2trunk.gameserver.taskmanager;

import l2trunk.gameserver.taskmanager.TaskManager.ExecutedTask;

import java.util.concurrent.ScheduledFuture;

public abstract class Task {
    public abstract void initializate();

    ScheduledFuture<?> launchSpecial(ExecutedTask instance) {
        return null;
    }

    public abstract String getName();

    public abstract void onTimeElapsed(ExecutedTask task);

    void onDestroy() {
    }
}