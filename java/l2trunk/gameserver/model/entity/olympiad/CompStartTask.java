package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompStartTask extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(CompStartTask.class);

    @Override
    public void runImpl() {
        if (Olympiad.isOlympiadEnd())
            return;

        Olympiad._manager = new OlympiadManager();
        Olympiad._inCompPeriod = true;

        new Thread(Olympiad._manager).start();

        ThreadPoolManager.INSTANCE.schedule(new CompEndTask(), Olympiad.getMillisToCompEnd());

        Announcements.INSTANCE.announceToAll(new SystemMessage2(SystemMsg.THE_OLYMPIAD_GAME_HAS_STARTED));
        _log.info("Olympiad System: Olympiad Game Started");
    }
}