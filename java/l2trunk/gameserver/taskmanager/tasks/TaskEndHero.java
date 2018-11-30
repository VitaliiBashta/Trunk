package l2trunk.gameserver.taskmanager.tasks;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.taskmanager.Task;
import l2trunk.gameserver.taskmanager.TaskManager;
import l2trunk.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2trunk.gameserver.taskmanager.TaskTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskEndHero extends Task {
    private static final Logger _log = LoggerFactory.getLogger(TaskEndHero.class);
    private static final String NAME = "TaskEndHero";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(ExecutedTask task) {
        _log.info("Hero End Global Task: launched.");
        for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
            if (player.getVarLong("HeroPeriod") <= System.currentTimeMillis()) {
                player.setHero(false);
                player.updatePledgeClass();
                player.broadcastUserInfo(true);
                Hero.deleteHero(player);
                Hero.removeSkills(player);
                player.unsetVar("HeroPeriod");
            }
        }
        _log.info("Hero End Global Task: completed.");
    }

    @Override
    public void initializate() {
        TaskManager.INSTANCE.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
    }
}