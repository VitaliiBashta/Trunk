package l2trunk.gameserver.instancemanager.naia;


import l2trunk.commons.geometry.Polygon;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public enum NaiaCoreManager {
    INSTANCE;
    private static final Territory _coreTerritory = new Territory()
            .add(new Polygon().add(-44789, 246305)
                    .add(-44130, 247452)
                    .add(-46092, 248606)
                    .add(-46790, 247414)
                    .add(-46139, 246304)
                    .setZmin(-14220)
                    .setZmax(-13800));
    //Spores
    private static final int fireSpore = 25605;
    private static final int waterSpore = 25606;
    private static final int windSpore = 25607;
    private static final int earthSpore = 25608;
    //Bosses
    private static final int fireEpidos = 25609;
    private static final int waterEpidos = 25610;
    private static final int windEpidos = 25611;
    private static final int earthEpidos = 25612;
    private static final int teleCube = 32376;
    private static final int respawnDelay = 120; // 2min
    private static final long coreClearTime = 60 * 60 * 1000L; // 1hour
    private static final Location spawnLoc = new Location(-45496, 246744, -14209);
    private static Zone _zone;
    private static boolean _active = false;
    private static boolean bossSpawned = false;
    private final Logger _log = LoggerFactory.getLogger(NaiaTowerManager.class);
    List<Integer> spores = List.of(fireSpore, waterSpore, windSpore, earthSpore);
    List<Integer> epidoses = List.of(fireEpidos, waterEpidos, windEpidos, earthEpidos);

    private static void spawnToRoom(List<Integer> mobIds) {
        for (int mobId : mobIds)
            for (int i = 0; i < 10; i++) {
                SimpleSpawner sp = new SimpleSpawner(mobId);
                sp.setLoc(Territory.getRandomLoc(NaiaCoreManager._coreTerritory).setH(Rnd.get(65535)));
                sp.setRespawnDelay(respawnDelay, 30);
                sp.setAmount(1);
                sp.doSpawn(true);
                sp.startRespawn();
            }
    }

    private boolean isActive() {
        return _active;
    }

    public void setZoneActive(boolean value) {
        _zone.setActive(value);
    }

    public void spawnEpidos(int index) {
        if (!isActive())
            return;
        int epidostospawn = 0;
        switch (index) {
            case 1: {
                epidostospawn = fireEpidos;
                break;
            }
            case 2: {
                epidostospawn = waterEpidos;
                break;
            }
            case 3: {
                epidostospawn = windEpidos;
                break;
            }
            case 4: {
                epidostospawn = earthEpidos;
                break;
            }
            default:
                break;
        }
        SimpleSpawner sp = new SimpleSpawner(epidostospawn);
        sp.setLoc(spawnLoc);
        sp.doSpawn(true);
        sp.stopRespawn();
        bossSpawned = true;
    }

    public boolean isBossSpawned() {
        return bossSpawned;
    }

    public void setBossSpawned(boolean value) {
        bossSpawned = value;
    }

    public void removeSporesAndSpawnCube() {

        GameObjectsStorage.getAllByNpcId(spores, false).forEach(GameObject::deleteMe);
        Spawner sp = new SimpleSpawner(teleCube)
                .setLoc(spawnLoc)
                .stopRespawn();
        sp.doSpawn(true);
        Functions.npcShout(sp.getLastSpawn(), "Teleportation to Beleth Throne Room is available for 5 minutes");
    }

    public void launchNaiaCore() {
        if (isActive())
            return;

        _active = true;
        ReflectionUtils.getDoor(18250025).closeMe();
        _zone.setActive(true);
        spawnSpores();
        ThreadPoolManager.INSTANCE.schedule(new ClearCore(), coreClearTime);
    }

    private void spawnSpores() {
        spawnToRoom(spores);
    }

    public void init() {
        _zone = ReflectionUtils.getZone("[naia_core_poison]");
        _log.info("Naia Core Manager: Loaded");
    }

    private class ClearCore extends RunnableImpl {
        @Override
        public void runImpl() {


            GameObjectsStorage.getAllByNpcId(spores, false).forEach(GameObject::deleteMe);
            GameObjectsStorage.getAllByNpcId(epidoses, false).forEach(GameObject::deleteMe);

            _active = false;
            ReflectionUtils.getDoor(18250025).openMe();
            _zone.setActive(false);
        }
    }
}