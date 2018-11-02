package l2f.gameserver.taskmanager;

import l2f.gameserver.taskmanager.TaskManager.ExecutedTask;

import java.util.concurrent.ScheduledFuture;

public abstract class Task {
    public abstract void initializate();

    public ScheduledFuture<?> launchSpecial(ExecutedTask instance) {
        return null;
    }

    public abstract String getName();

    public abstract void onTimeElapsed(ExecutedTask task);

    public void onDestroy() {
    }
}