package l2trunk.gameserver.instancemanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.mapregion.RestartArea;
import l2trunk.gameserver.templates.mapregion.RestartPoint;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public enum AutoSpawnManager {
    INSTANCE;
    private static final int DEFAULT_INITIAL_SPAWN = 30000; // 30 seconds after registration
    private static final int DEFAULT_RESPAWN = 3600000; //1 hour in millisecs
    private static final int DEFAULT_DESPAWN = 3600000; //1 hour in millisecs
    private final Logger _log = LoggerFactory.getLogger(AutoSpawnManager.class);
    private final Map<Integer, AutoSpawnInstance> registeredSpawns;
    private final Map<Integer, ScheduledFuture<?>> runningSpawns;

    AutoSpawnManager() {
        registeredSpawns = new ConcurrentHashMap<>();
        runningSpawns = new ConcurrentHashMap<>();

        restoreSpawnData();

        _log.info("AutoSpawnHandler: Loaded " + registeredSpawns.size() + " handlers in total.");
    }

    public static AutoSpawnManager INSTANCE() {
        return INSTANCE;

    }

    private void restoreSpawnData() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             // Restore spawn group data, then the location data.
             PreparedStatement statement = con.prepareStatement("SELECT * FROM random_spawn ORDER BY groupId ASC");
             PreparedStatement statement2 = con.prepareStatement("SELECT * FROM random_spawn_loc WHERE groupId=?");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                // Register random spawn group, set various options on the created spawn instance.
                AutoSpawnInstance spawnInst = registerSpawn(rset.getInt("npcId"), rset.getInt("initialDelay"), rset.getInt("respawnDelay"), rset.getInt("despawnDelay"));
                spawnInst.setSpawnCount(rset.getInt("count"));
                spawnInst.setRandomSpawn(rset.getBoolean("randomSpawn"));

                // Restore the spawn locations for this spawn group/instance.

                statement2.setInt(1, rset.getInt("groupId"));
                ResultSet rset2 = statement2.executeQuery();
                while (rset2.next())
                    // Add each location to the spawn group/instance.
                    spawnInst.addSpawnLocation(new Location(rset2.getInt("x"), rset2.getInt("y"), rset2.getInt("z"), rset2.getInt("heading")));
            }
        } catch (SQLException e) {
            _log.warn("AutoSpawnHandler: Could not restore spawn data: ", e);
        }
    }

    /**
     * Registers a spawn with the given parameters with the spawner, and marks it as
     * active. Returns a AutoSpawnInstance containing info about the spawn.
     *
     * @return AutoSpawnInstance spawnInst
     */
    private AutoSpawnInstance registerSpawn(int npcId, int initialDelay, int respawnDelay, int despawnDelay) {
        if (initialDelay < 0)
            initialDelay = DEFAULT_INITIAL_SPAWN;

        if (respawnDelay < 0)
            respawnDelay = DEFAULT_RESPAWN;

        if (despawnDelay < 0)
            despawnDelay = DEFAULT_DESPAWN;

        AutoSpawnInstance newSpawn = new AutoSpawnInstance(npcId, initialDelay, respawnDelay, despawnDelay);

        int newId = IdFactory.getInstance().getNextId();
        newSpawn.objectId = newId;
        registeredSpawns.put(newId, newSpawn);

        setSpawnActive(newSpawn, true);

        return newSpawn;
    }


    /**
     * Sets the active state of the specified spawn.
     */
    public void setSpawnActive(AutoSpawnInstance spawnInst, boolean isActive) {
        int objectId = spawnInst.objectId;

        if (isSpawnRegistered(objectId)) {
            ScheduledFuture<?> spawnTask;

            if (isActive) {
                AutoSpawner rset = new AutoSpawner(objectId);
                if (spawnInst.despawnDelay > 0)
                    spawnTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(rset, spawnInst.initDelay, spawnInst.resDelay);
                else
                    spawnTask = ThreadPoolManager.INSTANCE.schedule(rset, spawnInst.initDelay);
                //spawnTask = ThreadPoolManager.INSTANCE().scheduleGeneralAtFixedRate(rset, spawnInst.initDelay, spawnInst.resDelay);
                runningSpawns.put(objectId, spawnTask);
            } else {
                spawnTask = runningSpawns.remove(objectId);

                if (spawnTask != null)
                    spawnTask.cancel(false);
            }

            spawnInst.setSpawnActive(isActive);
        }
    }

    /**
     * Attempts to return the AutoSpawnInstance associated with the given NPC or Object ID type.
     * <BR>
     * Note: If isObjectId == false, returns first instance for the specified NPC ID.
     *
     * @return AutoSpawnInstance spawnInst
     */
    public final AutoSpawnInstance getAutoSpawnInstance(int id, boolean isObjectId) {
        if (isObjectId) {
            if (isSpawnRegistered(id))
                return registeredSpawns.get(id);
        } else
            for (AutoSpawnInstance spawnInst : registeredSpawns.values()            )
                if (spawnInst.npcId == id)
                    return spawnInst;

        return null;
    }

    public Map<Integer, AutoSpawnInstance> getAllAutoSpawnInstance(int id) {
        Map<Integer, AutoSpawnInstance> spawnInstList = new ConcurrentHashMap<>();

        registeredSpawns.values().stream()
                .filter(s -> s.npcId == id)
                .forEach(s -> spawnInstList.put(s.objectId, s));

        return spawnInstList;
    }

    /**
     * Tests if the specified object ID is assigned to an auto spawn.
     */
    private boolean isSpawnRegistered(int objectId) {
        return registeredSpawns.containsKey(objectId);
    }

    /**
     * AutoSpawner Class
     * <BR><BR>
     * This handles the main spawn task for an auto spawn instance, and initializes
     * a despawner if required.
     *
     * @author Tempy
     */
    private class AutoSpawner extends RunnableImpl {
        private final int objectId;

        AutoSpawner(int objectId) {
            this.objectId = objectId;
        }

        @Override
        public void runImpl() {
            try {
                // Retrieve the required spawn instance for this spawn task.
                AutoSpawnInstance spawnInst = registeredSpawns.get(objectId);

                // If the spawn is not scheduled to be active, cancel the spawn task.
                if (!spawnInst.isSpawnActive() || Config.DONTLOADSPAWN)
                    return;

                List<Location> locationList = spawnInst.getLocationList();

                // If there are no set co-ordinates, cancel the spawn task.
                if (locationList.size() == 0) {
                    LOG.info("AutoSpawnHandler: No location co-ords specified for spawn instance (Object ID = " + objectId + ").");
                    return;
                }

                int locationCount = locationList.size();
                int locationIndex = Rnd.get(locationCount);

                /*
                 * If random spawning is disabled, the spawn at the next set of
                 * co-ordinates after the last. If the index is greater than the number
                 * of possible spawns, reset the counter to zero.
                 */
                if (!spawnInst.isRandomSpawn()) {
                    locationIndex = spawnInst.lastLocIndex;
                    locationIndex++;

                    if (locationIndex == locationCount)
                        locationIndex = 0;

                    spawnInst.lastLocIndex = locationIndex;
                }

                // Fetch the template for this NPC ID and create a new spawn.
                SimpleSpawner newSpawn = new SimpleSpawner(spawnInst.npcId);

                newSpawn.setLoc(locationList.get(locationIndex))
                        .setAmount(spawnInst.spawnCount);
                if (spawnInst.despawnDelay == 0)
                    newSpawn.setRespawnDelay(spawnInst.resDelay);

                // Add the new spawn information to the spawn table, but do not store it.
                NpcInstance npcInst = newSpawn.doSpawn(true);

                for (int i = 0; i < spawnInst.spawnCount; i++) {
                    npcInst = newSpawn.doSpawn(true);

                    // To prevent spawning of more than one NPC in the exact same spot,
                    // move it slightly by a small random offset.
                    npcInst.setLoc(Location.findPointToStay(npcInst, 50));

                    // Add the NPC instance to the list of managed instances.
                    spawnInst.addAttackable(npcInst);
                }

                RestartArea ra = MapRegionHolder.getInstance().getRegionData(RestartArea.class, npcInst);


                if (spawnInst.despawnDelay > 0) {
                    AutoDespawner rd = new AutoDespawner(objectId);
                    ThreadPoolManager.INSTANCE.schedule(rd, spawnInst.despawnDelay - 1000);
                }
            } catch (RuntimeException e) {
                LOG.warn("AutoSpawnHandler: An error occurred while initializing spawn instance (Object ID = " +
                        objectId + "): ", e);
            }
        }
    }

    private class AutoDespawner extends RunnableImpl {
        private final int objectId;

        AutoDespawner(int objectId) {
            this.objectId = objectId;
        }

        @Override
        public void runImpl() {
            try {
                AutoSpawnInstance spawnInst = registeredSpawns.get(objectId);

                spawnInst.getAttackableList().forEach(npcInst -> {
                    npcInst.deleteMe();
                    spawnInst.removeAttackable(npcInst);
                });
            } catch (RuntimeException e) {
                LOG.warn("AutoSpawnHandler: An error occurred while despawning spawn (Object ID = " + objectId + "): ", e);
            }
        }
    }

    public class AutoSpawnInstance {
        final int initDelay;
        final int resDelay;
        final int despawnDelay;
        private final int npcId;
        private final List<NpcInstance> npcList = new ArrayList<>();
        private final List<Location> locList = new ArrayList<>();
        int objectId;
        int spawnCount = 1;
        int lastLocIndex = -1;
        private boolean spawnActive;
        private boolean randomSpawn = false;

        AutoSpawnInstance(int npcId, int initDelay, int respawnDelay, int despawnDelay) {
            this.npcId = npcId;
            this.initDelay = initDelay;
            resDelay = respawnDelay;
            this.despawnDelay = despawnDelay;
        }

        void addAttackable(NpcInstance npcInst) {
            npcList.add(npcInst);
        }

        void removeAttackable(NpcInstance npcInst) {
            npcList.remove(npcInst);
        }

        public int getObjectId() {
            return objectId;
        }

        void setSpawnCount(int spawnCount) {
            this.spawnCount = spawnCount;
        }

        List<Location> getLocationList() {
            return locList;
        }

        List<NpcInstance> getAttackableList() {
            return npcList;
        }

        public List<Spawner> getSpawns() {
            List<Spawner> npcSpawns = new ArrayList<>();

            for (NpcInstance npcInst : npcList)
                npcSpawns.add(npcInst.getSpawn());

            return npcSpawns;
        }


        public boolean isSpawnActive() {
            return spawnActive;
        }

        void setSpawnActive(boolean activeValue) {
            spawnActive = activeValue;
        }

        boolean isRandomSpawn() {
            return randomSpawn;
        }

        void setRandomSpawn(boolean randValue) {
            randomSpawn = randValue;
        }

        void addSpawnLocation(Location loc) {
            locList.add(loc);
        }

    }
}
