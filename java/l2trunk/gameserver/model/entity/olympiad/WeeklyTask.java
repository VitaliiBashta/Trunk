package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Doing Weekly Task(giving points and new fight to all Noble Players)
 */
class WeeklyTask implements Runnable {
    private static final Logger _log = LoggerFactory.getLogger(WeeklyTask.class);

    @Override
    public void run() {
        Olympiad.doWeekTasks();
        _log.info("Olympiad System: Added weekly points to nobles");

        Calendar nextChange = Calendar.getInstance();
        Olympiad.nextWeeklyChange = nextChange.getTimeInMillis() + Config.ALT_OLY_WPERIOD;
    }
}