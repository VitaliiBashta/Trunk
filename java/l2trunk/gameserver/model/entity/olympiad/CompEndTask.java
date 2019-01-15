package l2trunk.gameserver.model.entity.olympiad;


import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CompEndTask extends RunnableImpl {
    private static final Logger LOG = LoggerFactory.getLogger(CompEndTask.class);

    @Override
    public void runImpl() {
        if (Olympiad.isOlympiadEnd())
            return;

        Olympiad._inCompPeriod = false;
        OlympiadManager manager = Olympiad._manager;

        // Если остались игры, ждем их завершения еще одну минуту
        if (manager != null && !manager.getOlympiadGames().isEmpty()) {
            ThreadPoolManager.INSTANCE.schedule(new CompEndTask(), 60000);
            return;
        }

        Announcements.INSTANCE.announceToAll(new SystemMessage2(SystemMsg.THE_OLYMPIAD_GAME_HAS_ENDED));
        LOG.info("Olympiad System: Olympiad Game Ended");
        OlympiadDatabase.save();
        Olympiad.init();
    }
}