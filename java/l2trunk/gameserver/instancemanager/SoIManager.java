package l2trunk.gameserver.instancemanager;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class SoIManager {
    private static final Logger _log = LoggerFactory.getLogger(SoIManager.class);
    private static final long SOI_OPEN_TIME = 24 * 60 * 60 * 1000L;
    private static final List<Location> openSeedTeleportLocs = List.of(
            new Location(-179537, 209551, -15504),
            new Location(-179779, 212540, -15520),
            new Location(-177028, 211135, -15520),
            new Location(-176355, 208043, -15520),
            new Location(-179284, 205990, -15520),
            new Location(-182268, 208218, -15520),
            new Location(-182069, 211140, -15520),
            new Location(-176036, 210002, -11948),
            new Location(-176039, 208203, -11949),
            new Location(-183288, 208205, -11939),
            new Location(-183290, 210004, -11939),
            new Location(-187776, 205696, -9536),
            new Location(-186327, 208286, -9536),
            new Location(-184429, 211155, -9536),
            new Location(-182811, 213871, -9504),
            new Location(-180921, 216789, -9536),
            new Location(-177264, 217760, -9536),
            new Location(-173727, 218169, -9536));
    private static Zone _zone = null;

    public static void init() {
        _log.info("Seed of Infinity Manager: Loaded. Current stage is: " + getCurrentStage());
        _zone = ReflectionUtils.getZone("[inner_undying01]");
        checkStageAndSpawn();
        if (isSeedOpen())
            openSeed(getOpenedTime());
    }

    public static int getCurrentStage() {
        return ServerVariables.getInt("SoI_stage", 1);
    }

    public static void setCurrentStage(int stage) {
        if (getCurrentStage() == stage)
            return;
        if (stage == 3)
            openSeed(SOI_OPEN_TIME);
        else if (isSeedOpen())
            closeSeed();
        ServerVariables.set("SoI_stage", stage);
        setCohemenesCount(0);
        setEkimusCount(0);
        setHoEDefCount(0);
        checkStageAndSpawn();
        _log.info("Seed of Infinity Manager: Set to stage " + stage);
    }

    private static long getOpenedTime() {
        if (getCurrentStage() != 3)
            return 0;
        return ServerVariables.getLong("SoI_opened", 0) * 1000L - System.currentTimeMillis();
    }

    public static boolean isSeedOpen() {
        return getOpenedTime() > 0;
    }

    private static void openSeed(long time) {
        if (time <= 0)
            return;
        ServerVariables.set("SoI_opened", (System.currentTimeMillis() + time) / 1000L);
        _log.info("Seed of Infinity Manager: Opening the seed for " + Util.formatTime((int) time / 1000));
        spawnOpenedSeed();
        ReflectionUtils.getDoor(14240102).openMe();

        ThreadPoolManager.INSTANCE.schedule(() -> {
            closeSeed();
            setCurrentStage(4);
        }, time);
    }

    private static void closeSeed() {
        _log.info("Seed of Infinity Manager: Closing the seed.");
        ServerVariables.unset("SoI_opened");
        //Despawning tumors / seeds
        SpawnManager.INSTANCE.despawn("soi_hos_middle_seeds");
        SpawnManager.INSTANCE.despawn("soi_hoe_middle_seeds");
        SpawnManager.INSTANCE.despawn("soi_hoi_middle_seeds");
        SpawnManager.INSTANCE.despawn("soi_all_middle_stable_tumor");
        ReflectionUtils.getDoor(14240102).closeMe();
        getZone().getInsidePlayables().forEach(p ->
                p.teleToLocation(getZone().getRestartPoints().get(0)));
    }

    private static void checkStageAndSpawn() {
        SpawnManager.INSTANCE.despawn("soi_world_closedmouths");
        SpawnManager.INSTANCE.despawn("soi_world_mouths");
        SpawnManager.INSTANCE.despawn("soi_world_abyssgaze2");
        SpawnManager.INSTANCE.despawn("soi_world_abyssgaze1");
        switch (getCurrentStage()) {
            case 1:
            case 4:
                SpawnManager.INSTANCE.spawn("soi_world_mouths");
                SpawnManager.INSTANCE.spawn("soi_world_abyssgaze2");
                break;
            case 5:
                SpawnManager.INSTANCE.spawn("soi_world_closedmouths");
                SpawnManager.INSTANCE.spawn("soi_world_abyssgaze2");
                break;
            default:
                SpawnManager.INSTANCE.spawn("soi_world_closedmouths");
                SpawnManager.INSTANCE.spawn("soi_world_abyssgaze1");
                break;
        }
    }

    private static Zone getZone() {
        return _zone;
    }

    public static void notifyCohemenesKill() {
        if (getCurrentStage() == 1) {
            if (getCohemenesCount() < 9)
                setCohemenesCount(getCohemenesCount() + 1);
            else
                setCurrentStage(2);
        }
    }

    public static void notifyEkimusKill() {
        if (getCurrentStage() == 2) {
            if (getEkimusCount() < 2)
                setEkimusCount(getEkimusCount() + 1);
            else
                setCurrentStage(3);
        }
    }

    public static void notifyHoEDefSuccess() {
        if (getCurrentStage() == 4) {
            if (getHoEDefCount() < 9)
                setHoEDefCount(getHoEDefCount() + 1);
            else
                setCurrentStage(5);
        }
    }

    private static int getCohemenesCount() {
        return ServerVariables.getInt("SoI_CohemenesCount", 0);
    }

    private static void setCohemenesCount(int i) {
        ServerVariables.set("SoI_CohemenesCount", i);
    }

    private static int getEkimusCount() {
        return ServerVariables.getInt("SoI_EkimusCount", 0);
    }

    private static void setEkimusCount(int i) {
        ServerVariables.set("SoI_EkimusCount", i);
    }

    private static int getHoEDefCount() {
        return ServerVariables.getInt("SoI_hoedefkillcount", 0);
    }

    private static void setHoEDefCount(int i) {
        ServerVariables.set("SoI_hoedefkillcount", i);
    }

    private static void spawnOpenedSeed() {
        SpawnManager.INSTANCE.spawn("soi_hos_middle_seeds");
        SpawnManager.INSTANCE.spawn("soi_hoe_middle_seeds");
        SpawnManager.INSTANCE.spawn("soi_hoi_middle_seeds");
        SpawnManager.INSTANCE.spawn("soi_all_middle_stable_tumor");
    }

    public static void teleportInSeed(Player p) {
        p.teleToLocation(Rnd.get(openSeedTeleportLocs));
    }
}