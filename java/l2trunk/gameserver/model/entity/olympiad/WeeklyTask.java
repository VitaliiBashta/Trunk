package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Doing Weekly Task(giving points and new fight to all Noble Players)
 */
public class WeeklyTask implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(WeeklyTask.class);

    @Override
    public void run() {
        Olympiad.doWeekTasks();
        LOG.info("Olympiad System: Added weekly points to nobles");

        Calendar nextChange = Calendar.getInstance();
        Olympiad.nextWeeklyChange = nextChange.getTimeInMillis() + Config.ALT_OLY_WPERIOD;
    }
}