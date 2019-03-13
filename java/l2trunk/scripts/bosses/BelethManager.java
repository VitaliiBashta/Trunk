package l2trunk.scripts.bosses;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public final class BelethManager extends Functions implements ScriptFile {
    private static final Logger LOG = LoggerFactory.getLogger(BelethManager.class);
    private static final List<Player> INDEXED_PLAYERS = new ArrayList<>();
    private static final List<NpcInstance> NPC_LIST = new ArrayList<>();
    private static final int DOOR_WAIT_TIME_DURATION = 60000; // 1min
    private static final int SPAWN_WAIT_TIME_DURATION = 120000; // 2min
    private static final int CLOSE_DOOR_TIME_DURATION = 180000; // 3min
    private static final int CLONES_RESPAWN_TIME_TIME_DURATION = 40 * 1000; // 40 sec default
    private static final int RING_AVAILABLE_TIME = 300000; // 5min
    private static final int CLEAR_ENTITY_TIME = 600000; // 10min
    private static final long BELETH_RESPAWN_TIME = 2 * 24 * 60 * 60 * 1000; // 2 days
    private static final long ENTITY_INACTIVITY_TIME = 2 * 60 * 60 * 1000; // 2 hours
    private static final int RING_SPAWN_TIME = 300000; // 5min
    private static final int LAST_SPAWN_TIME = 600000; // 10min
    private static final int DOOR = 20240001; // Throne door
    private static final int CORRDOOR = 20240002; // Corridor door
    private static final int COFFDOOR = 20240003; // Tomb door
    private static final int VORTEX = 29125; // Vortex.
    private static final int ELF = 29128; // Elf corpse.
    private static final int COFFIN = 32470; // Beleth's coffin.
    private static final int BELETH = 29118; // Beleth.
    private static final int CLONE = 29119; // Beleth's newInstance.
    private static final Location VORTEXSPAWN = Location.of(16325, 214983, -9353, 16384);
    private static final Location COFFSPAWN = Location.of(12471, 215602, -9360, 49152);
    private static final Location BELSPAWN = Location.of(16325, 214614, -9353, 49152);
    private static final int centerX = 16325; // Center of the room
    private static final int centerY = 213135;
    private static final Map<MonsterInstance, Location> CLONES = new ConcurrentHashMap<>();
    private static final Location[] CLONE_LOC = new Location[56];
    private static BelethManager instance;
    private static boolean taskStarted = false;
    private static boolean entryLocked = false;
    private static boolean ringAvailable = false;
    private static boolean belethAlive = false;
    private static RaidBossInstance beleth = null;
    private static ScheduledFuture<?> cloneRespawnTask;
    private static ScheduledFuture<?> ringSpawnTask;
    private static ScheduledFuture<?> lastSpawnTask;
    private static ScheduledFuture<?> elpyThread = null;
    private static NpcInstance elpy = null;
    private final Zone zone = ReflectionUtils.getZone("[Beleth_room]");
    private final ZoneListener zoneListener = new ZoneListener();

    public static BelethManager getInstance() {
        if (instance == null)
            instance = new BelethManager();
        return instance;
    }

    private static NpcInstance spawn(int npcId, Location loc) {
        NpcTemplate template = NpcHolder.getTemplate(npcId);
        NpcInstance npc = template.getNewInstance();
        npc.setSpawnedLoc(loc);
        npc.setLoc(loc);
        npc.setHeading(loc.h);
        npc.spawnMe();
        return npc;
    }

    private static boolean checkPlayer(Player player) {
        return !player.isDead() && player.getLevel() >= 80;
    }

    private static boolean checkBossSpawnCond() {
        if (INDEXED_PLAYERS.size() < 18 || taskStarted) // 18 players
            return false;

        return ServerVariables.getLong("BelethKillTime") <= System.currentTimeMillis();
    }

    public static boolean isRingAvailable() {
        return ringAvailable;
    }

    public static void setRingAvailable(boolean ringAvailable) {
        BelethManager.ringAvailable = ringAvailable;
    }

    private static void spawnClone(int id) {
        MonsterInstance clone;
        clone = (MonsterInstance) spawn(CLONE, CLONE_LOC[id]); // CLONE_LOC[i].h
        CLONES.put(clone, clone.getLoc());
    }

    private static void initSpawnLocs() {
        // Variables for Calculations
        double angle = Math.toRadians(22.5);
        int radius = 700;

        // Inner newInstance circle
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0)
                radius -= 50;
            else
                radius += 50;
            CLONE_LOC[i] = Location.of(centerX + (int) (radius * Math.sin(i * angle)), centerY + (int) (radius * Math.cos(i * angle)), -9353, PositionUtils.convertDegreeToClientHeading(270 - i * 22.5));
        }
        // Outer newInstance square
        radius = 1340;
        int mulX, mulY, addH = 3;
        double decX, decY;
        for (int i = 0; i < 16; i++) {
            if (i % 8 == 0)
                mulX = 0;
            else if (i < 8)
                mulX = -1;
            else
                mulX = 1;
            if (i == 4 || i == 12)
                mulY = 0;
            else if (i > 4 && i < 12)
                mulY = -1;
            else
                mulY = 1;
            if (i % 8 == 1 || i == 7 || i == 15)
                decX = 0.5;
            else
                decX = 1.0;
            if (i % 10 == 3 || i == 5 || i == 11)
                decY = 0.5;
            else
                decY = 1.0;
            if ((i + 2) % 4 == 0)
                addH++;
            CLONE_LOC[i + 16] = Location.of(centerX + (int) (radius * decX * mulX), centerY + (int) (radius * decY * mulY), -9353, PositionUtils.convertDegreeToClientHeading(180 + addH * 90));
        }
        // Octagon #2 - Another ring of clones like the inner square, that
        // spawns after some time
        angle = Math.toRadians(22.5);
        radius = 1000;
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0)
                radius -= 70;
            else
                radius += 70;
            CLONE_LOC[i + 32] = Location.of(centerX + (int) (radius * Math.sin(i * angle)), centerY + (int) (radius * Math.cos(i * angle)), -9353, CLONE_LOC[i].h);
        }
        // Extra clones - Another 8 clones that spawn when beleth is close to
        // dying
        int order = 48;
        radius = 650;
        for (int i = 1; i < 16; i += 2) {
            if (i == 1 || i == 15)
                CLONE_LOC[order] = Location.of(CLONE_LOC[i].x, CLONE_LOC[i].y + radius, -9360, CLONE_LOC[i + 16].h);
            else if (i == 3 || i == 5)
                CLONE_LOC[order] = new Location(CLONE_LOC[i].x + radius, CLONE_LOC[i].y, -9360, CLONE_LOC[i].h);
            else if (i == 7 || i == 9)
                CLONE_LOC[order] = new Location(CLONE_LOC[i].x, CLONE_LOC[i].y - radius, -9360, CLONE_LOC[i + 16].h);
            else if (i == 11 || i == 13)
                CLONE_LOC[order] = new Location(CLONE_LOC[i].x - radius, CLONE_LOC[i].y, -9360, CLONE_LOC[i].h);
            order++;
        }
    }

    private static void checkElpySpawn() {
        if (ServerVariables.getLong("BelethKillTime") > System.currentTimeMillis() && elpyThread == null) {
            elpyThread = ThreadPoolManager.INSTANCE.schedule(BelethManager::spawnElpy, (ServerVariables.getLong("BelethKillTime") - System.currentTimeMillis()));
        } else if (ServerVariables.getLong("BelethKillTime") < System.currentTimeMillis() && elpy == null) {
            spawnElpy();
        }
    }

    public static void setElpyDead() {
        elpy = null;
        elpyThread = ThreadPoolManager.INSTANCE.schedule(BelethManager::checkElpySpawn, 2 * 60 * 60 * 1000);
    }

    private static void spawnElpy() {
        elpy = NpcUtils.spawnSingle(25604, Location.of(-45480, 246824, -14209, 49152));
    }

    public void setBelethDead() {
        if (entryLocked && belethAlive) {
            ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.beleth_dead), 10);
            checkElpySpawn();
        }
    }

    @Override
    public void onLoad() {
        zone.addListener(zoneListener);
        checkElpySpawn();
        LOG.info("Beleth Manager: Loaded successfuly");
    }

    @Override
    public void onReload() {
        zone.removeListener(zoneListener);
    }

    private enum Event {
        none,
        start,
        open_door,
        close_door,
        beleth_spawn,
        beleth_despawn,
        clone_despawn,
        clone_spawn,
        ring_unset,
        beleth_dead,
        entity_clear,
        inactivity_check,
        spawn_ring,
        spawn_extras
    }

    private static class CloneRespawnTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (CLONES.isEmpty())
                return;

            MonsterInstance nextclone;
            for (MonsterInstance clone : CLONES.keySet())
                if (clone.isDead() || clone.isDeleted()) {
                    nextclone = (MonsterInstance) spawn(CLONE, CLONES.get(clone)); // CLONE_LOC[i].h
                    CLONES.put(nextclone, nextclone.getLoc());
                    CLONES.remove(clone);
                }
        }
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {

        @Override
        public void onZoneEnter(Zone zone, Player player) {
            if (player == null || entryLocked)
                return;

            if (!INDEXED_PLAYERS.contains(player))
                if (checkPlayer(player))
                    INDEXED_PLAYERS.add(player);

            if (checkBossSpawnCond()) {
                ThreadPoolManager.INSTANCE.schedule(new BelethSpawnTask(), 10000L);
                taskStarted = true;
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Player player) {
            INDEXED_PLAYERS.remove(player);
        }
    }

    private class BelethSpawnTask extends RunnableImpl {
        @Override
        public void runImpl() {
            INDEXED_PLAYERS.clear();
            ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.start), 10000L);
            ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.inactivity_check), ENTITY_INACTIVITY_TIME);
            initSpawnLocs();
        }
    }

    private class EventExecutor extends RunnableImpl {
        final Event event;

        EventExecutor(Event event) {
            this.event = event;
        }

        @Override
        public void runImpl() {
            switch (event) {
                case start:
                    ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.open_door), DOOR_WAIT_TIME_DURATION);
                    break;
                case open_door:
                    ReflectionUtils.getDoor(DOOR).openMe();
                    ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.close_door), CLOSE_DOOR_TIME_DURATION);
                    break;
                case close_door:
                    ReflectionUtils.getDoor(DOOR).closeMe();
                    entryLocked = true;
                    ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.beleth_spawn), SPAWN_WAIT_TIME_DURATION);
                    break;
                case beleth_spawn:
                    NpcInstance temp = spawn(VORTEX, VORTEXSPAWN);
                    NPC_LIST.add(temp);
                    beleth = (RaidBossInstance) spawn(BELETH, BELSPAWN);
                    beleth.startImmobilized();
                    belethAlive = true;
                    ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.clone_spawn), 10); // initial clones
                    ringSpawnTask = ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.spawn_ring), RING_SPAWN_TIME); // inner ring
                    lastSpawnTask = ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.spawn_extras), LAST_SPAWN_TIME); // last clones
                    break;
                case clone_spawn:
                    MonsterInstance clone;
                    for (int i = 0; i < 32; i++) {
                        clone = (MonsterInstance) spawn(CLONE, CLONE_LOC[i]); // CLONE_LOC[i].h
                        CLONES.put(clone, clone.getLoc());
                    }
                    if (CLONES_RESPAWN_TIME_TIME_DURATION > 0)
                        cloneRespawnTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new CloneRespawnTask(), CLONES_RESPAWN_TIME_TIME_DURATION, CLONES_RESPAWN_TIME_TIME_DURATION);
                    break;
                case spawn_ring:
                    for (int i = 32; i < 48; i++)
                        spawnClone(i);
                    break;
                case spawn_extras:
                    for (int i = 48; i < 56; i++)
                        spawnClone(i);
                    break;
                case beleth_dead:
                    if (cloneRespawnTask != null) {
                        cloneRespawnTask.cancel(false);
                        cloneRespawnTask = null;
                    }
                    if (ringSpawnTask != null) {
                        ringSpawnTask.cancel(false);
                        ringSpawnTask = null;
                    }
                    if (lastSpawnTask != null) {
                        lastSpawnTask.cancel(false);
                        lastSpawnTask = null;
                    }
                    temp = spawn(ELF, beleth.getLoc());
                    NPC_LIST.add(temp);
                    temp = spawn(COFFIN, COFFSPAWN);
                    NPC_LIST.add(temp);
                    ReflectionUtils.getDoor(CORRDOOR).openMe();
                    ReflectionUtils.getDoor(COFFDOOR).openMe();
                    setRingAvailable(true);
                    belethAlive = false;
                    ServerVariables.set("BelethKillTime", System.currentTimeMillis() + BELETH_RESPAWN_TIME);
                    zone.getInsidePlayers().forEach(i ->
                            i.sendMessage("Beleth's Lair will push you out in 10 minutes"));
                    ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.clone_despawn), 10);
                    ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.ring_unset), RING_AVAILABLE_TIME);
                    ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.entity_clear), CLEAR_ENTITY_TIME);
                    break;
                case ring_unset:
                    setRingAvailable(false);
                    break;
                case entity_clear:
                    for (NpcInstance n : NPC_LIST)
                        if (n != null)
                            n.deleteMe();
                    NPC_LIST.clear();

                    // Close coffin and corridor doors
                    ReflectionUtils.getDoor(CORRDOOR).closeMe();
                    ReflectionUtils.getDoor(COFFDOOR).closeMe();

                    //oust players
                    zone.getInsidePlayers().forEach(i -> {
                        i.teleToLocation(new Location(-11802, 236360, -3271));
                        i.sendMessage("Beleth's Lair has become unstable so you've been teleported out");
                    });
                    entryLocked = false;
                    taskStarted = false;
                    break;
                case clone_despawn:
                    for (MonsterInstance clonetodelete : CLONES.keySet())
                        clonetodelete.deleteMe();
                    CLONES.clear();
                    break;
                case inactivity_check:
                    if (!beleth.isDead()) {
                        beleth.deleteMe();
                        ThreadPoolManager.INSTANCE.schedule(new EventExecutor(Event.entity_clear), 10);
                    }
                    break;
            }
        }
    }

}