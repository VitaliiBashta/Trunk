package l2trunk.gameserver.model.entity.olympiad;


import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OlympiadEndTask extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(OlympiadEndTask.class);

    @Override
    public void runImpl() {
        if (Olympiad._inCompPeriod) { // Если бои еще не закончились, откладываем окончание олимпиады на минуту
            ThreadPoolManager.INSTANCE.schedule(new OlympiadEndTask(), 60000);
            return;
        }

        Announcements.INSTANCE.announceToAll(new SystemMessage2(SystemMsg.OLYMPIAD_PERIOD_S1_HAS_ENDED).addInteger(Olympiad._currentCycle));
        Announcements.INSTANCE.announceToAll("Olympiad Validation Period has began");

        Olympiad._isOlympiadEnd = true;
        if (Olympiad._scheduledManagerTask != null)
            Olympiad._scheduledManagerTask.cancel(false);
        if (Olympiad._scheduledWeeklyTask != null)
            Olympiad._scheduledWeeklyTask.cancel(false);

        Olympiad._validationEnd = Olympiad._olympiadEnd + Config.ALT_OLY_VPERIOD;

        OlympiadDatabase.saveNobleData();
        Olympiad._period = 1;
        Hero.INSTANCE.clearHeroes();

        try {
            OlympiadDatabase.save();
        } catch (Exception e) {
            _log.error("Olympiad System: Failed to save Olympiad configuration!", e);
        }

        _log.info("Olympiad System: Starting Validation period. Time to end validation:" + Olympiad.getMillisToValidationEnd() / (60 * 1000));

        if (Olympiad._scheduledValdationTask != null)
            Olympiad._scheduledValdationTask.cancel(false);
        Olympiad._scheduledValdationTask = ThreadPoolManager.INSTANCE.schedule(new ValidationTask(), Olympiad.getMillisToValidationEnd());
    }
}