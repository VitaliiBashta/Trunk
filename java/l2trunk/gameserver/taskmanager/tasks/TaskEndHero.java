package l2trunk.gameserver.taskmanager.tasks;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.taskmanager.Task;
import l2trunk.gameserver.taskmanager.TaskManager;
import l2trunk.gameserver.taskmanager.TaskManager.ExecutedTask;
import l2trunk.gameserver.taskmanager.TaskTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TaskEndHero extends Task {
    private static final Logger LOG = LoggerFactory.getLogger(TaskEndHero.class);
    private static final String NAME = "TaskEndHero";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void onTimeElapsed(ExecutedTask task) {
        LOG.info("Hero End Global Task: launched.");
        GameObjectsStorage.getAllPlayersStream()
                .filter(p -> p.getVarLong("HeroPeriod") <= System.currentTimeMillis())
                .forEach(p -> {
                    p.setHero(false);
                    p.updatePledgeClass();
                    p.broadcastUserInfo(true);
                    Hero.deleteHero(p);
                    Hero.removeSkills(p);
                    p.unsetVar("HeroPeriod");
                });
        LOG.info("Hero End Global Task: completed.");
    }

    @Override
    public void initializate() {
        TaskManager.INSTANCE.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "06:30:00", "");
    }
}