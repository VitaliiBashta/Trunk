package l2trunk.scripts.events.Christmas;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Calendar;


public class NewYearTimer implements ScriptFile {
    private static NewYearTimer instance;

    public static NewYearTimer getInstance() {
        if (instance == null)
            new NewYearTimer();
        return instance;
    }

    public NewYearTimer() {
        if (instance != null)
            return;

        instance = this;

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

        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("С new, " + c.get(Calendar.YEAR) + ", year!!!"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("1"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("2"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("3"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("4"), getDelay(c));
        c.add(Calendar.SECOND, -1);
        ThreadPoolManager.getInstance().schedule(new NewYearAnnouncer("5"), getDelay(c));
    }

    private long getDelay(Calendar c) {
        return c.getTime().getTime() - System.currentTimeMillis();
    }

    /**
     * Вызывается при загрузке классов скриптов
     */
    @Override
    public void onLoad() {
    }

    /**
     * Вызывается при перезагрузке
     * После перезагрузки onLoad() вызывается автоматически
     */
    @Override
    public void onReload() {
    }

    /**
     * Читает статус эвента из базы.
     *
     * @return
     */
    private static boolean isActive() {
        return ServerVariables.getString("Christmas", "off").equalsIgnoreCase("on");
    }

    /**
     * Вызывается при выключении сервера
     */
    @Override
    public void onShutdown() {
    }

    private class NewYearAnnouncer extends RunnableImpl {
        private final String message;

        private NewYearAnnouncer(String message) {
            this.message = message;
        }

        @SuppressWarnings("unused")
        @Override
        public void runImpl() {
            Announcements.getInstance().announceToAll(message);

            // Через жопу сделано, но не суть важно :)
            if (message.length() == 1)
                return;

            for (Player player : GameObjectsStorage.getAllPlayersForIterate()) {
                Skill skill = SkillTable.getInstance().getInfo(3266, 1);
                MagicSkillUse msu = new MagicSkillUse(player, player, 3266, 1, skill.getHitTime(), 0);
                player.broadcastPacket(msu);
            }

            instance = null;
            new NewYearTimer();
        }
    }
}