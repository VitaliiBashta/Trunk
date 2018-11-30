package l2trunk.gameserver.instancemanager;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.mapregion.RestartArea;
import l2trunk.gameserver.templates.mapregion.RestartPoint;
import l2trunk.gameserver.templates.npc.NpcTemplate;
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
import java.util.concurrent.TimeUnit;

public class AutoSpawnManager {
    private static final Logger _log = LoggerFactory.getLogger(AutoSpawnManager.class);
    private static final int DEFAULT_INITIAL_SPAWN = 30000; // 30 seconds after registration
    private static final int DEFAULT_RESPAWN = 3600000; //1 hour in millisecs
    private static final int DEFAULT_DESPAWN = 3600000; //1 hour in millisecs
    private static AutoSpawnManager _instance;
    private final Map<Integer, AutoSpawnInstance> _registeredSpawns;
    private final Map<Integer, ScheduledFuture<?>> _runningSpawns;

    private AutoSpawnManager() {
        _registeredSpawns = new ConcurrentHashMap<>();
        _runningSpawns = new ConcurrentHashMap<>();

        restoreSpawnData();

        _log.info("AutoSpawnHandler: Loaded " + size() + " handlers in total.");
    }

    public static AutoSpawnManager getInstance() {
        if (_instance == null)
            _instance = new AutoSpawnManager();

        return _instance;
    }

    private int size() {
        return _registeredSpawns.size();
    }

    private void restoreSpawnData() {
        int numLoaded = 0;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             // Restore spawn group data, then the location data.
             PreparedStatement statement = con.prepareStatement("SELECT * FROM random_spawn ORDER BY groupId ASC");
             PreparedStatement statement2 = con.prepareStatement("SELECT * FROM random_spawn_loc WHERE groupId=?");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                // Register random spawn group, set various options on the created spawn instance.
                AutoSpawnInstance spawnInst = registerSpawn(rset.getInt("npcId"), rset.getInt("initialDelay"), rset.getInt("respawnDelay"), rset.getInt("despawnDelay"));
                spawnInst.setSpawnCount(rset.getInt("count"));
                spawnInst.setBroadcast(rset.getBoolean("broadcastSpawn"));
                spawnInst.setRandomSpawn(rset.getBoolean("randomSpawn"));
                numLoaded++;

                // Restore the spawn locations for this spawn group/instance.

                statement2.setInt(1, rset.getInt("groupId"));
                ResultSet rset2 = statement2.executeQuery();
                while (rset2.next())
                    // Add each location to the spawn group/instance.
                    spawnInst.addSpawnLocation(rset2.getInt("x"), rset2.getInt("y"), rset2.getInt("z"), rset2.getInt("heading"));
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
    private AutoSpawnInstance registerSpawn(int npcId, int[][] spawnPoints, int initialDelay, int respawnDelay, int despawnDelay) {
        if (initialDelay < 0)
            initialDelay = DEFAULT_INITIAL_SPAWN;

        if (respawnDelay < 0)
            respawnDelay = DEFAULT_RESPAWN;

        if (despawnDelay < 0)
            despawnDelay = DEFAULT_DESPAWN;

        AutoSpawnInstance newSpawn = new AutoSpawnInstance(npcId, initialDelay, respawnDelay, despawnDelay);

        if (spawnPoints != null)
            for (int[] spawnPoint : spawnPoints)
                newSpawn.addSpawnLocation(spawnPoint);

        int newId = IdFactory.getInstance().getNextId();
        newSpawn.objectId = newId;
        _registeredSpawns.put(newId, newSpawn);

        setSpawnActive(newSpawn, true);

        return newSpawn;
    }

    /**
     * Registers a spawn with the given parameters with the spawner, and marks it as
     * active. Returns a AutoSpawnInstance containing info about the spawn.
     * <B>Warning:</B> Spawn locations must be specified separately using addSpawnLocation().
     * <p>
     * npcId
     * initialDelay (If < 0 = default value)
     * respawnDelay (If < 0 = default value)
     * despawnDelay (If < 0 = default value or if = 0, function disabled)
     *
     * @return AutoSpawnInstance spawnInst
     */
    private AutoSpawnInstance registerSpawn(int npcId, int initialDelay, int respawnDelay, int despawnDelay) {
        return registerSpawn(npcId, null, initialDelay, respawnDelay, despawnDelay);
    }

    /**
     * Remove a registered spawn from the list, specified by the given spawn instance.
     */
    private boolean removeSpawn(AutoSpawnInstance spawnInst) {
        if (!isSpawnRegistered(spawnInst))
            return false;

        try {
            // Try to remove from the list of registered spawns if it exists.
            _registeredSpawns.remove(spawnInst.getNpcId());

            // Cancel the currently associated running scheduled task.
            ScheduledFuture<?> respawnTask = _runningSpawns.remove(spawnInst.objectId);
            respawnTask.cancel(false);
        } catch (RuntimeException e) {
            _log.warn("AutoSpawnHandler: Could not auto spawn for NPC ID " + spawnInst._npcId + " (Object ID = " + spawnInst.objectId + "): ", e);
            return false;
        }

        return true;
    }

    /**
     * Remove a registered spawn from the list, specified by the given spawn object ID.
     */
    public void removeSpawn(int objectId) {
        removeSpawn(_registeredSpawns.get(objectId));
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
                if (spawnInst._desDelay > 0)
                    spawnTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(rset, spawnInst.initDelay, spawnInst.resDelay);
                else
                    spawnTask = ThreadPoolManager.INSTANCE.schedule(rset, spawnInst.initDelay);
                //spawnTask = ThreadPoolManager.INSTANCE().scheduleGeneralAtFixedRate(rset, spawnInst.initDelay, spawnInst.resDelay);
                _runningSpawns.put(objectId, spawnTask);
            } else {
                spawnTask = _runningSpawns.remove(objectId);

                if (spawnTask != null)
                    spawnTask.cancel(false);
            }

            spawnInst.setSpawnActive(isActive);
        }
    }

    /**
     * Returns the number of milliseconds until the next occurrance of
     * the given spawn.
     */
    public final long getTimeToNextSpawn(AutoSpawnInstance spawnInst) {
        int objectId = spawnInst.objectId;

        if (!isSpawnRegistered(objectId))
            return -1;

        return _runningSpawns.get(objectId).getDelay(TimeUnit.MILLISECONDS);
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
                return _registeredSpawns.get(id);
        } else
            for (AutoSpawnInstance spawnInst : _registeredSpawns.values())
                if (spawnInst._npcId == id)
                    return spawnInst;

        return null;
    }

    public Map<Integer, AutoSpawnInstance> getAllAutoSpawnInstance(int id) {
        Map<Integer, AutoSpawnInstance> spawnInstList = new ConcurrentHashMap<>();

        for (AutoSpawnInstance spawnInst : _registeredSpawns.values())
            if (spawnInst._npcId == id)
                spawnInstList.put(spawnInst.objectId, spawnInst);

        return spawnInstList;
    }

    /**
     * Tests if the specified object ID is assigned to an auto spawn.
     */
    private boolean isSpawnRegistered(int objectId) {
        return _registeredSpawns.containsKey(objectId);
    }

    /**
     * Tests if the specified spawn instance is assigned to an auto spawn.
     */
    private boolean isSpawnRegistered(AutoSpawnInstance spawnInst) {
        return _registeredSpawns.containsValue(spawnInst);
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
        private final int _objectId;

        AutoSpawner(int objectId) {
            _objectId = objectId;
        }

        @Override
        public void runImpl() {
            try {
                // Retrieve the required spawn instance for this spawn task.
                AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

                // If the spawn is not scheduled to be active, cancel the spawn task.
                if (!spawnInst.isSpawnActive() || Config.DONTLOADSPAWN)
                    return;

                Location[] locationList = spawnInst.getLocationList();

                // If there are no set co-ordinates, cancel the spawn task.
                if (locationList.length == 0) {
                    _log.info("AutoSpawnHandler: No location co-ords specified for spawn instance (Object ID = " + _objectId + ").");
                    return;
                }

                int locationCount = locationList.length;
                int locationIndex = Rnd.get(locationCount);

                /*
                 * If random spawning is disabled, the spawn at the next set of
                 * co-ordinates after the last. If the index is greater than the number
                 * of possible spawns, reset the counter to zero.
                 */
                if (!spawnInst.isRandomSpawn()) {
                    locationIndex = spawnInst._lastLocIndex;
                    locationIndex++;

                    if (locationIndex == locationCount)
                        locationIndex = 0;

                    spawnInst._lastLocIndex = locationIndex;
                }

                // Set the X, Y and Z co-ordinates, where this spawn will take place.
                final int x = locationList[locationIndex].x;
                final int y = locationList[locationIndex].y;
                final int z = locationList[locationIndex].z;
                final int heading = locationList[locationIndex].h;

                // Fetch the template for this NPC ID and create a new spawn.
                NpcTemplate npcTemp = NpcHolder.getInstance().getTemplate(spawnInst.getNpcId());
                SimpleSpawner newSpawn = new SimpleSpawner(npcTemp);

                newSpawn.setLocx(x);
                newSpawn.setLocy(y);
                newSpawn.setLocz(z);
                if (heading != -1)
                    newSpawn.setHeading(heading);
                newSpawn.setAmount(spawnInst.getSpawnCount());
                if (spawnInst._desDelay == 0)
                    newSpawn.setRespawnDelay(spawnInst.resDelay);

                // Add the new spawn information to the spawn table, but do not store it.
                NpcInstance npcInst = null;

                for (int i = 0; i < spawnInst._spawnCount; i++) {
                    npcInst = newSpawn.doSpawn(true);

                    // To prevent spawning of more than one NPC in the exact same spot,
                    // move it slightly by a small random offset.
                    npcInst.setXYZ(npcInst.getX() + Rnd.get(50), npcInst.getY() + Rnd.get(50), npcInst.getZ());

                    // Add the NPC instance to the list of managed instances.
                    spawnInst.addAttackable(npcInst);
                }

                RestartArea ra = MapRegionManager.getInstance().getRegionData(RestartArea.class, npcInst.getLoc());
                String nearestTown = "";
                if (ra != null) {
                    RestartPoint rp = ra.getRestartPoint().get(Race.human);
                    nearestTown = rp.getNameLoc();
                }

                // Announce to all players that the spawn has taken place, with the nearest town location.
				/*if (spawnInst.isBroadcasting() && npcInst != null)
					Announcements.INSTANCE().announceByCustomMessage("l2trunk.gameserver.model.AutoSpawnHandler.spawnNPC", new String[] {
							npcInst.getName(),
							nearestTown });*/

                // If there is no despawn time, do not create a despawn task.
                if (spawnInst.getDespawnDelay() > 0) {
                    AutoDespawner rd = new AutoDespawner(_objectId);
                    ThreadPoolManager.INSTANCE.schedule(rd, spawnInst.getDespawnDelay() - 1000);
                }
            } catch (RuntimeException e) {
                _log.warn("AutoSpawnHandler: An error occurred while initializing spawn instance (Object ID = " +
                        _objectId + "): ", e);
            }
        }
    }

    /**
     * AutoDespawner Class
     * <BR><BR>
     * Simply used as a secondary class for despawning an auto spawn instance.
     *
     * @author Tempy
     */
    private class AutoDespawner extends RunnableImpl {
        private final int _objectId;

        AutoDespawner(int objectId) {
            _objectId = objectId;
        }

        @Override
        public void runImpl() {
            try {
                AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

                for (NpcInstance npcInst : spawnInst.getAttackableList()) {
                    npcInst.deleteMe();
                    spawnInst.removeAttackable(npcInst);
                }
            } catch (RuntimeException e) {
                _log.warn("AutoSpawnHandler: An error occurred while despawning spawn (Object ID = " + _objectId + "): ", e);
            }
        }
    }

    /**
     * AutoSpawnInstance Class
     * <BR><BR>
     * Stores information about a registered auto spawn.
     *
     * @author Tempy
     */
    public class AutoSpawnInstance {
        final int _npcId;
        final int initDelay;
        final int resDelay;
        final int _desDelay;
        private final List<NpcInstance> npcList = new ArrayList<>();
        private final List<Location> _locList = new ArrayList<>();
        protected int _spawnIndex;
        int objectId;
        int _spawnCount = 1;
        int _lastLocIndex = -1;
        private boolean _spawnActive;
        private boolean _randomSpawn = false;
        private boolean _broadcastAnnouncement = false;

        AutoSpawnInstance(int npcId, int initDelay, int respawnDelay, int despawnDelay) {
            _npcId = npcId;
            this.initDelay = initDelay;
            resDelay = respawnDelay;
            _desDelay = despawnDelay;
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

        public int getInitialDelay() {
            return initDelay;
        }

        public int getRespawnDelay() {
            return resDelay;
        }

        int getDespawnDelay() {
            return _desDelay;
        }

        int getNpcId() {
            return _npcId;
        }

        int getSpawnCount() {
            return _spawnCount;
        }

        void setSpawnCount(int spawnCount) {
            _spawnCount = spawnCount;
        }

        Location[] getLocationList() {
            return _locList.toArray(new Location[_locList.size()]);
        }

        NpcInstance[] getAttackableList() {
            return npcList.toArray(new NpcInstance[npcList.size()]);
        }

        public Spawner[] getSpawns() {
            List<Spawner> npcSpawns = new ArrayList<>();

            for (NpcInstance npcInst : npcList)
                npcSpawns.add(npcInst.getSpawn());

            return npcSpawns.toArray(new Spawner[npcSpawns.size()]);
        }

        public void setBroadcast(boolean broadcastValue) {
            _broadcastAnnouncement = broadcastValue;
        }

        public boolean isSpawnActive() {
            return _spawnActive;
        }

        void setSpawnActive(boolean activeValue) {
            _spawnActive = activeValue;
        }

        boolean isRandomSpawn() {
            return _randomSpawn;
        }

        void setRandomSpawn(boolean randValue) {
            _randomSpawn = randValue;
        }

        public boolean isBroadcasting() {
            return _broadcastAnnouncement;
        }

        boolean addSpawnLocation(int x, int y, int z, int heading) {
            return _locList.add(new Location(x, y, z, heading));
        }

        boolean addSpawnLocation(int[] spawnLoc) {
            if (spawnLoc.length != 3)
                return false;

            return addSpawnLocation(spawnLoc[0], spawnLoc[1], spawnLoc[2], -1);
        }

        public Location removeSpawnLocation(int locIndex) {
            try {
                return _locList.remove(locIndex);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }
}
