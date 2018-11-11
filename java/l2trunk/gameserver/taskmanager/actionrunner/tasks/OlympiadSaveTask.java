package l2trunk.gameserver.taskmanager.actionrunner.tasks;

import l2trunk.gameserver.model.entity.olympiad.OlympiadDatabase;

public final class OlympiadSaveTask extends AutomaticTask {
    public OlympiadSaveTask() {
        super();
    }

    @Override
    public void doTask() {
        OlympiadDatabase.save();
    }

    @Override
    public long reCalcTime(boolean start) {
        return System.currentTimeMillis() + 600000L;
    }
}
