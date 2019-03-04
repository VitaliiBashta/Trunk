package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public final class SoDManager {
    private static final String SPAWN_GROUP = "sod_free";
    private static final Logger LOG = LoggerFactory.getLogger(SoDManager.class);
    private static final long SOD_OPEN_TIME = 12 * 60 * 60 * 1000L;
    private static SoDManager _instance;
    private static Zone zone;
    private static boolean isOpened = false;

    private SoDManager() {
        LOG.info("Seed of Destruction Manager: Loaded");
        zone = ReflectionUtils.getZone("[inner_destruction01]");
        if (!isAttackStage())
            openSeed(getOpenedTimeLimit());
    }

    public static SoDManager getInstance() {
        if (_instance == null)
            _instance = new SoDManager();
        return _instance;
    }

    public static boolean isAttackStage() {
        return getOpenedTimeLimit() <= 0;
    }

    public static void addTiatKill() {
        if (!isAttackStage())
            return;
        if (ServerVariables.getInt("Tial_kills") < 9)
            ServerVariables.inc("Tial_kills");
        else
            openSeed(SOD_OPEN_TIME);
    }

    private static long getOpenedTimeLimit() {
        return ServerVariables.getLong("SoD_opened") * 1000L - System.currentTimeMillis();
    }

    private static Zone getZone() {
        return zone;
    }

    public static void teleportIntoSeed(Player p) {
        p.teleToLocation(new Location(-245800, 220488, -12112));
    }

    private static void openDoors() {
        IntStream.rangeClosed(12240003, 12240031).forEach(door -> ReflectionUtils.getDoor(door).openMe());
    }

    private static void closeDoors() {
        IntStream.rangeClosed(12240003, 12240031).forEach(door -> ReflectionUtils.getDoor(door).closeMe());
    }

    public static void openSeed(long timelimit) {
        if (isOpened)
            return;
        isOpened = true;
        ServerVariables.unset("Tial_kills");
        ServerVariables.set("SoD_opened", (System.currentTimeMillis() + timelimit) / 1000L);
        // spawns
        // handle zone + task
        LOG.info("Seed of Destruction Manager: Opening the seed for " + Util.formatTime((int) timelimit / 1000));
        SpawnManager.INSTANCE.spawn(SPAWN_GROUP);
        openDoors();

        ThreadPoolManager.INSTANCE.schedule(SoDManager::closeSeed, timelimit);
    }

    public static void closeSeed() {
        if (!isOpened) return;
        isOpened = false;
        LOG.info("Seed of Destruction Manager: Closing the seed.");
        ServerVariables.unset("SoD_opened");
        SpawnManager.INSTANCE.despawn(SPAWN_GROUP);
        // Телепортируем всех внутри СоДа наружу
        getZone().getInsidePlayables().forEach(p ->
                p.teleToLocation(getZone().getRestartPoints().get(0)));
        closeDoors();
    }
}