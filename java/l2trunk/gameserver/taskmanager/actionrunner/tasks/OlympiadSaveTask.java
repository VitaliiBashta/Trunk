package l2trunk.gameserver.taskmanager.actionrunner.tasks;

import l2trunk.gameserver.model.entity.olympiad.OlympiadDatabase;

public final class OlympiadSaveTask extends AutomaticTask {

    @Override
    public void doTask() {
        OlympiadDatabase.save();
    }

}
