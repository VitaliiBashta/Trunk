package l2trunk.scripts.events.Christmas;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;

import java.util.Calendar;

public enum  NewYearTimer {
    INSTANCE;
    private static final int firework = 3266;

    NewYearTimer() {

        if (!isActive())
            return;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        while (getDelay(c) < 0)
            c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);

        ThreadPoolManager.INSTANCE.schedule(new NewYearAnnouncer("С new, " + c.get(Calendar.YEAR) + ", year!!!"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.INSTANCE.schedule(new NewYearAnnouncer("1"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.INSTANCE.schedule(new NewYearAnnouncer("2"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.INSTANCE.schedule(new NewYearAnnouncer("3"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.INSTANCE.schedule(new NewYearAnnouncer("4"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.INSTANCE.schedule(new NewYearAnnouncer("5"), getDelay(c));
    }

    private static boolean isActive() {
        return ServerVariables.getString("Christmas", "off").equalsIgnoreCase("on");
    }

    private long getDelay(Calendar c) {
        return c.getTime().getTime() - System.currentTimeMillis();
    }

    private class NewYearAnnouncer extends RunnableImpl {
        private final String message;

        private NewYearAnnouncer(String message) {
            this.message = message;
        }

        @Override
        public void runImpl() {
            Announcements.INSTANCE.announceToAll(message);

            if (message.length() == 1)
                return;
            GameObjectsStorage.getAllPlayersStream().forEach(pl -> pl.broadcastPacket(new MagicSkillUse(pl, firework)));

        }
    }
}