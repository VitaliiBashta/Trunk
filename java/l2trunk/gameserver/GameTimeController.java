package l2trunk.gameserver;

import l2trunk.commons.listener.ListenerList;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.listener.GameListener;
import l2trunk.gameserver.listener.game.OnDayNightChangeListener;
import l2trunk.gameserver.listener.game.OnStartListener;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.network.serverpackets.ClientSetTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

public enum GameTimeController {
    INSTANCE;
    private static final int TICKS_PER_SECOND = 10;
    private static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
    private final Logger _log = LoggerFactory.getLogger(GameTimeController.class);
    private final GameTimeListenerList listenerEngine = new GameTimeListenerList();
    private final Runnable dayChangeNotify = new CheckSunState();
    private long gameStartTime;

    public void init() {
        gameStartTime = getDayStartTime();

        GameServer.getInstance().addListener(new OnStartListenerImpl());

        StringBuilder msg = new StringBuilder();
        msg.append("GameTimeController: initialized.").append(" ");
        msg.append("Current time is ");
        msg.append(getGameHour()).append(":");
        if (getGameMin() < 10)
            msg.append("0");
        msg.append(getGameMin());
        msg.append(" in the ");
        if (isNowNight())
            msg.append("night");
        else
            msg.append("day");
        msg.append(".");

        _log.info(msg.toString());

        long nightStart = 0;
        long dayStart = 60 * 60 * 1000;

        while (gameStartTime + nightStart < System.currentTimeMillis())
            nightStart += 4 * 60 * 60 * 1000;

        while (gameStartTime + dayStart < System.currentTimeMillis())
            dayStart += 4 * 60 * 60 * 1000;

        dayStart -= System.currentTimeMillis() - gameStartTime;
        nightStart -= System.currentTimeMillis() - gameStartTime;

        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(dayChangeNotify, nightStart, 4 * 60 * 60 * 1000L);
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(dayChangeNotify, dayStart, 4 * 60 * 60 * 1000L);
    }

    /**
     * Вычисляем смещение до начала игровых суток
     *
     * @return смещение в миллисекнду до начала игровых суток (6:00AM)
     */
    private long getDayStartTime() {
        Calendar dayStart = Calendar.getInstance();

        int HOUR_OF_DAY = dayStart.get(Calendar.HOUR_OF_DAY);

        dayStart.add(Calendar.HOUR_OF_DAY, -(HOUR_OF_DAY + 1) % 4); //1 день в игре это 4 часа реального времени
        dayStart.set(Calendar.MINUTE, 0);
        dayStart.set(Calendar.SECOND, 0);
        dayStart.set(Calendar.MILLISECOND, 0);

        return dayStart.getTimeInMillis();
    }

    public boolean isNowNight() {
        return getGameHour() < 6;
    }

    public int getGameTime() {
        return getGameTicks() / MILLIS_IN_TICK;
    }

    public int getGameHour() {
        return getGameTime() / 60 % 24;
    }

    public int getGameMin() {
        return getGameTime() % 60;
    }

    private int getGameTicks() {
        return (int) ((System.currentTimeMillis() - gameStartTime) / MILLIS_IN_TICK);
    }

    private GameTimeListenerList getListenerEngine() {
        return listenerEngine;
    }

    public <T extends GameListener> void addListener(T listener) {
        listenerEngine.add(listener);
    }

    public <T extends GameListener> boolean removeListener(T listener) {
        return listenerEngine.remove(listener);
    }

    private class OnStartListenerImpl implements OnStartListener {
        @Override
        public void onStart() {
            ThreadPoolManager.INSTANCE.execute(dayChangeNotify);
        }
    }

    private class CheckSunState extends RunnableImpl {
        @Override
        public void runImpl() {
            if (isNowNight())
                INSTANCE.getListenerEngine().onNight();
            else
                INSTANCE.getListenerEngine().onDay();

            GameObjectsStorage.getAllPlayersStream().forEach(player -> {
                player.checkDayNightMessages();
                player.sendPacket(ClientSetTime.STATIC);
            });
        }
    }

    protected class GameTimeListenerList extends ListenerList {
        void onDay() {
            getListeners().stream().filter(l -> l instanceof OnDayNightChangeListener)
                    .map(l -> (OnDayNightChangeListener) l)
                    .forEach(OnDayNightChangeListener::onDay);
        }

        void onNight() {
            getListeners().stream().filter(l -> l instanceof OnDayNightChangeListener)
                    .map(l -> (OnDayNightChangeListener) l)
                    .forEach(OnDayNightChangeListener::onNight);
        }
    }
}