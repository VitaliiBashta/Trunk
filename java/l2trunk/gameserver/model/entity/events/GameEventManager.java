package l2trunk.gameserver.model.entity.events;

import l2trunk.gameserver.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

public enum GameEventManager {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(GameEventManager.class);
    private final HashMap<String, GameEvent> _events = new HashMap<>();
    private ScheduledFuture<?> event_sched;
    private GameEvent event;

    private GameEventManager() {
        event_sched = null;
        event = null;
    }


    public void registerEvent(GameEvent evt) {
        _events.put(evt.getName(), evt);
        nextEvent();
    }

    private boolean nextEvent() {
        long min = 0;
        long time = 0;
        GameEvent nextEv = null;
        for (GameEvent ev : _events.values()) {
            time = ev.getNextTime();
            if (min > time || min == 0)
                min = time;
        }
        for (GameEvent ev : _events.values()) {
            time = ev.getNextTime();
            if (min == time) {
                nextEv = ev;
                break;
            }
        }

        if (event_sched != null)
            event_sched.cancel(true);

        event = nextEv;

        if (event == null) {
            _log.info("Event loadFile: error");
            return false;
        }

        _log.info("Event " + event.getName() + " started in " + Long.toString((time - System.currentTimeMillis() / 1000) / 60) + " mins.");
        event_sched = ThreadPoolManager.INSTANCE.schedule(new EventStart(), time * 1000 - System.currentTimeMillis());
        return true;
    }

    private class EventStart implements Runnable {
        private EventStart() {
        }

        public void run() {
            if (event != null)
                event.start();
            _log.info("Event " + event.getName() + " has been started.");
        }
    }
}