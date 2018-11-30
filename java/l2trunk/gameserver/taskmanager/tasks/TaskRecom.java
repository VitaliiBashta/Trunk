package l2trunk.gameserver.taskmanager.tasks;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.taskmanager.Task;
import l2trunk.gameserver.taskmanager.TaskManager;
import l2trunk.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2trunk.gameserver.taskmanager.TaskTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRecom extends Task {
    private static final Logger _log = LoggerFactory.getLogger(TaskRecom.class);
    private static final String NAME = "sp_recommendations";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(ExecutedTask task) {
        _log.info("Recommendation Global Task: launched.");
        for (Player player : GameObjectsStorage.getAllPlayersForIterate())
            player.restartRecom();
        _log.info("Recommendation Global Task: completed.");
    }

    @Override
    public void initializate() {
        TaskManager.INSTANCE.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
    }
}