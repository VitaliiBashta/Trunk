package l2trunk.gameserver.model.entity;

import l2trunk.commons.listener.Listener;
import l2trunk.commons.listener.ListenerList;
import l2trunk.commons.threading.FutureManager;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.database.mysql;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.actor.door.impl.MasterOnOpenCloseListenerImpl;
import l2trunk.gameserver.listener.reflection.OnReflectionCollapseListener;
import l2trunk.gameserver.listener.zone.impl.AirshipControllerZoneListener;
import l2trunk.gameserver.listener.zone.impl.NoLandingZoneListener;
import l2trunk.gameserver.listener.zone.impl.ResidenceEnterLeaveListenerImpl;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.DoorTemplate;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.templates.ZoneTemplate;
import l2trunk.gameserver.templates.spawn.SpawnTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reflection {
    private final static AtomicInteger _nextId = new AtomicInteger();
    public final int id;
    protected final Lock lock = new ReentrantLock();
    protected final List<GameObject> objects = new CopyOnWriteArrayList<>();
    private final ReflectionListenerList listeners = new ReflectionListenerList();
    private final List<Spawner> spawns = new ArrayList<>();
    private final Set<Integer> visitors = new HashSet<>();
    int playerCount;
    // vars
    private Map<Integer, DoorInstance> doors = new HashMap<>();
    private Map<String, Zone> zones = Collections.emptyMap();
    private Map<String, List<Spawner>> spawners = Collections.emptyMap();
    private Party party;
    private CommandChannel _commandChannel;
    private String name = "";
    private InstantZone instance;
    private int geoIndex;
    private Location resetLoc; // место, к которому кидает при использовании SoE/unstuck, иначе выбрасывает в основной мир
    private Location returnLoc; // если не прописано reset, но прописан return, то телепортит туда, одновременно перемещая в основной мир
    private Location teleportLoc; // точка входа
    private int collapseIfEmptyTime;

    private boolean isCollapseStarted;
    private Future<?> collapseTask;
    private Future<?> _collapse1minTask;
    private Future<?> _hiddencollapseTask;

    public Reflection() {
        this(_nextId.incrementAndGet());
    }

    private Reflection(int id) {
        this.id = id;
    }

    /**
     * Только для статических рефлектов.
     *
     * @param id <= 0
     * @return ref
     */
    public static Reflection createReflection(int id) {
        if (id > 0) {
            throw new IllegalArgumentException("id should be <= 0");
        }

        return new Reflection(id);
    }

    public int getInstancedZoneId() {
        return instance == null ? 0 : instance.getId();
    }

    protected final Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setCommandChannel(CommandChannel commandChannel) {
        _commandChannel = commandChannel;
    }

    private void setCollapseIfEmptyTime(int value) {
        collapseIfEmptyTime = value;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public final InstantZone getInstancedZone() {
        return instance;
    }

    void setInstancedZone(InstantZone iz) {
        instance = iz;
    }

    public int getGeoIndex() {
        return geoIndex;
    }

    private void setGeoIndex(int geoIndex) {
        this.geoIndex = geoIndex;
    }

    public Location getCoreLoc() {
        return resetLoc;
    }

    public void setCoreLoc(Location l) {
        resetLoc = l;
    }

    public Location getReturnLoc() {
        return returnLoc;
    }

    public void setReturnLoc(Location l) {
        returnLoc = l;
    }

    public Location getTeleportLoc() {
        return teleportLoc;
    }

    void setTeleportLoc(Location l) {
        teleportLoc = l;
    }

    public List<Spawner> getSpawns() {
        return spawns;
    }

    public Collection<DoorInstance> getDoors() {
        return doors.values();
    }

    public DoorInstance getDoor(int id) {
        return doors.get(id);
    }

    public Zone getZone(String name) {
        if (!zones.containsKey(name)) {
            throw new IllegalArgumentException("not found zone for name1: " + name);
        }
        return zones.get(name);
    }

    /**
     * Время в мс
     */
    public void startCollapseTimer(long timeInMillis) {
        if (isDefault()) {
            new Exception("Basic reflection " + id + " could not be collapsed!").printStackTrace();
            return;
        }
        lock.lock();
        try {
            if (collapseTask != null) {
                collapseTask.cancel(false);
                collapseTask = null;
            }
            if (_collapse1minTask != null) {
                _collapse1minTask.cancel(false);
                _collapse1minTask = null;
            }
            collapseTask = ThreadPoolManager.INSTANCE.schedule(this::collapse, timeInMillis);

            if (timeInMillis >= (60 * 1000L)) {
                _collapse1minTask = ThreadPoolManager.INSTANCE.schedule(this::minuteBeforeCollapse, timeInMillis - (60 * 1000L));
            }
        } finally {
            lock.unlock();
        }
    }

    private void stopCollapseTimer() {
        lock.lock();
        try {
            FutureManager.cancel(collapseTask);
            if (collapseTask != null) {
                collapseTask.cancel(false);
                collapseTask = null;
            }

            if (_collapse1minTask != null) {
                _collapse1minTask.cancel(false);
                _collapse1minTask = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private synchronized void minuteBeforeCollapse() {
        if (isCollapseStarted) return;
        objects.stream()
                .filter(o -> o instanceof Player)
                .map(o -> (Player) o)
                .forEach(player -> {
                    if (player.isInParty())
                        player.updatePartyInstance();
                    else
                        player.updateSoloInstance();
                    player.sendPacket(new SystemMessage(SystemMessage.THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES).addNumber(1));
                });
    }

    public void collapse() {
        if (id <= 0) {
            new Exception("Basic reflection " + id + " could not be collapsed!").printStackTrace();
            return;
        }

        lock.lock();
        try {
            if (isCollapseStarted) {
                return;
            }
            isCollapseStarted = true;
        } finally {
            lock.unlock();
        }
        listeners.onCollapse();
        try {
            stopCollapseTimer();
            if (_hiddencollapseTask != null) {
                _hiddencollapseTask.cancel(false);
                _hiddencollapseTask = null;
            }

            spawns.forEach(Spawner::deleteAll);
            spawners.keySet().forEach(this::despawnByGroup);

            doors.values().forEach(GameObject::deleteMe);
            doors.clear();

            zones.values().forEach(zone -> zone.setActive(false));

            zones.clear();

            List<Player> teleport;
            List<GameObject> delete;

            lock.lock();
            try {
                teleport = objects.stream()
                        .filter(o -> o instanceof Player)
                        .map(o -> (Player) o)
                        .collect(Collectors.toList());

                delete = objects.stream()
                        .filter(o -> !(o instanceof Playable))
                        .collect(Collectors.toList());
            } finally {
                lock.unlock();
            }

            for (Player player : teleport) {
                if (player.getParty() != null) {
                    if (equals(player.getParty().getReflection())) {
                        player.getParty().setReflection(null);
                    }
                    if ((player.getParty().getCommandChannel() != null) && equals(player.getParty().getCommandChannel().getReflection())) {
                        player.getParty().getCommandChannel().setReflection(null);
                    }
                }
                if (equals(player.getReflection())) {
                    if (getReturnLoc() != null) {
                        player.teleToLocation(getReturnLoc(), ReflectionManager.DEFAULT);
                    } else {
                        player.setReflection(ReflectionManager.DEFAULT);
                    }
                }
            }

            if (_commandChannel != null) {
                _commandChannel.setReflection(null);
                _commandChannel = null;
            }

            if (party != null) {
                party.setReflection(null);
                party = null;
            }

            delete.forEach(GameObject::deleteMe);

            spawns.clear();
            objects.clear();
            visitors.clear();
            doors.clear();

            playerCount = 0;

            onCollapse();
        } finally {
            ReflectionManager.INSTANCE.remove(this);
            GeoEngine.FreeGeoIndex(getGeoIndex());
        }
    }

    protected void onCollapse() {
    }

    public void addObject(GameObject o) {
        if (isCollapseStarted) return;

        lock.lock();
        try {
            objects.add(o);
            if (o instanceof Player) {
                playerCount++;
                visitors.add(o.objectId());
                onPlayerEnter((Player) o);
            }
        } finally {
            lock.unlock();
        }
        if ((collapseIfEmptyTime > 0) && (_hiddencollapseTask != null)) {
            _hiddencollapseTask.cancel(false);
            _hiddencollapseTask = null;
        }
    }

    public void removeObject(GameObject o) {
        if (isCollapseStarted) {
            return;
        }

        lock.lock();
        try {
            if (!objects.remove(o)) {
                return;
            }
            if (o instanceof Player) {
                playerCount--;
                onPlayerExit((Player) o);
            }
        } finally {
            lock.unlock();
        }

        if ((playerCount <= 0) && !isDefault() && (_hiddencollapseTask == null)) {
            if (collapseIfEmptyTime <= 0) {
                collapse();
            } else {
                _hiddencollapseTask = ThreadPoolManager.INSTANCE.schedule(this::collapse, collapseIfEmptyTime * 60 * 1000L);
            }
        }
    }

    public void onPlayerEnter(Player player) {
        // Unequip forbidden for this instance items
        player.getInventory().validateItems();
    }

    public void onPlayerExit(Player player) {
        // Unequip forbidden for this instance items
        player.getInventory().validateItems();
    }

    public synchronized Stream<Player> getPlayers() {
        return objects.stream()
                .filter(o -> o instanceof Player)
                .map(o -> (Player) o);
    }

    public synchronized Stream<NpcInstance> getNpcs() {
        return objects.stream()
                .filter(o -> o instanceof NpcInstance)
                .map(o -> (NpcInstance) o);
    }

    public Stream<NpcInstance> getAllByNpcId(int npcId, boolean onlyAlive) {
        return objects.stream()
                .filter(o -> o instanceof NpcInstance)
                .map(o -> (NpcInstance) o)
                .filter(npc -> npcId == npc.getNpcId())
                .filter(npc -> (!onlyAlive || !npc.isDead()));
    }

    public boolean canChampions() {
        return id <= 0;
    }

    public boolean isAutolootForced() {
        return false;
    }

    public boolean isCollapseStarted() {
        return isCollapseStarted;
    }

    protected void addSpawn(SimpleSpawner spawn) {
        if (spawn != null) {
            spawns.add(spawn);
        }
    }

    private void fillSpawns(List<InstantZone.SpawnInfo> si) {
        if (si == null) {
            return;
        }
        for (InstantZone.SpawnInfo s : si) {
            SimpleSpawner c;
            switch (s.getSpawnType()) {
                case 0: // точечный спаун, в каждой указанной точке
                    for (Location loc : s.getCoords()) {
                        c = new SimpleSpawner(s.getNpcId());
                        c.setReflection(this);
                        c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
                        c.setAmount(s.getCount());
                        c.setLoc(loc);
                        c.doSpawn(true);
                        if (s.getRespawnDelay() == 0) {
                            c.stopRespawn();
                        } else {
                            c.startRespawn();
                        }
                        addSpawn(c);
                    }
                    break;
                case 1: // один точечный спаун в рандомной точке
                    c = new SimpleSpawner(s.getNpcId());
                    c.setReflection(this);
                    c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
                    c.setAmount(1);
                    c.setLoc(Rnd.get(s.getCoords()));
                    c.doSpawn(true);
                    if (s.getRespawnDelay() == 0) {
                        c.stopRespawn();
                    } else {
                        c.startRespawn();
                    }
                    addSpawn(c);
                    break;
                case 2: // локационный спаун
                    c = new SimpleSpawner(s.getNpcId());
                    c.setReflection(this);
                    c.setRespawnDelay(s.getRespawnDelay(), s.getRespawnRnd());
                    c.setAmount(s.getCount());
                    c.setTerritory(s.getTerritory());
                    for (int j = 0; j < s.getCount(); j++) {
                        c.doSpawn(true);
                    }
                    if (s.getRespawnDelay() == 0) {
                        c.stopRespawn();
                    } else {
                        c.startRespawn();
                    }
                    addSpawn(c);
            }
        }
    }

    // FIXME [VISTALL] сдвинуть в один?
    public void init(Map<Integer, DoorTemplate> doors, Map<String, ZoneTemplate> zones) {
        if (!doors.isEmpty()) {
            this.doors = new HashMap<>(doors.size());
        }

        for (DoorTemplate template : doors.values()) {
            DoorInstance door = new DoorInstance(IdFactory.getInstance().getNextId(), template);
            door.setReflection(this);
            door.setInvul(true);
            door.spawnMe(template.getLoc());
            if (template.isOpened()) {
                door.openMe();
            }

            this.doors.put(template.getNpcId(), door);
        }

        initDoors();

        if (!zones.isEmpty()) {
            this.zones = new HashMap<>(zones.size());
        }

        for (ZoneTemplate template : zones.values()) {
            Zone zone = new Zone(template);
            zone.setReflection(this);
            switch (zone.getType()) {
                case no_landing:
                case SIEGE:
                    zone.addListener(NoLandingZoneListener.STATIC);
                    break;
                case AirshipController:
                    zone.addListener(new AirshipControllerZoneListener());
                    break;
                case RESIDENCE:
                    zone.addListener(ResidenceEnterLeaveListenerImpl.STATIC);
                    break;
            }

            if (template.isEnabled()) {
                zone.setActive(true);
            }

            this.zones.put(template.getName(), zone);
        }
    }

    // FIXME [VISTALL] сдвинуть в один?
    private void init0(Map<Integer, InstantZone.DoorInfo> doors, Map<String, InstantZone.ZoneInfo> zones) {
        if (!doors.isEmpty()) {
            this.doors = new HashMap<>(doors.size());
        }

        for (InstantZone.DoorInfo info : doors.values()) {
            DoorInstance door = new DoorInstance(IdFactory.getInstance().getNextId(), info.getTemplate());
            door.setReflection(this);
            door.setInvul(info.isInvul());
            door.spawnMe(info.getTemplate().getLoc());
            if (info.isOpened()) {
                door.openMe();
            }

            this.doors.put(info.getTemplate().getNpcId(), door);
        }

        initDoors();

        if (!zones.isEmpty()) {
            this.zones = new HashMap<>(zones.size());
        }

        for (InstantZone.ZoneInfo t : zones.values()) {
            Zone zone = new Zone(t.getTemplate());
            zone.setReflection(this);
            switch (zone.getType()) {
                case no_landing:
                case SIEGE:
                    zone.addListener(NoLandingZoneListener.STATIC);
                    break;
                case AirshipController:
                    zone.addListener(new AirshipControllerZoneListener());
                    break;
                case RESIDENCE:
                    zone.addListener(ResidenceEnterLeaveListenerImpl.STATIC);
                    break;
            }

            if (t.isActive()) {
                zone.setActive(true);
            }

            this.zones.put(t.getTemplate().getName(), zone);
        }
    }

    private void initDoors() {
        doors.values().stream()
                .filter(door -> door.getTemplate().getMasterDoor() > 0)
                .forEach(door -> getDoor(door.getTemplate().getMasterDoor()).addListener(new MasterOnOpenCloseListenerImpl(door)));
    }

    public void openDoor(int doorId) {
        DoorInstance door = doors.get(doorId);
        if (door != null) {
            door.openMe();
        }
    }

    public void closeDoor(int doorId) {
        DoorInstance door = doors.get(doorId);
        if (door != null) {
            door.closeMe();
        }
    }

    /**
     * Удаляет все спауны из рефлекшена и запускает коллапс-таймер. Время указывается в минутах.
     */
    public void clearReflection(int timeInMinutes, boolean message) {
        if (isDefault()) {
            return;
        }
        getNpcs().forEach(GameObject::deleteMe);
        startCollapseTimer(timeInMinutes * 60 * 1000L);

        if (message) {
            getPlayers().filter(Objects::nonNull)
                    .forEach(pl ->
                            pl.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timeInMinutes)));
        }
    }

    public NpcInstance addSpawnWithoutRespawn(int npcId, GameObject object) {
        return addSpawnWithoutRespawn(npcId, object, 0);
    }

    public NpcInstance addSpawnWithoutRespawn(int npcId, GameObject object, int randomOffset) {
        return addSpawnWithoutRespawn(npcId, object.getLoc(), randomOffset);
    }

    public NpcInstance addSpawnWithoutRespawn(int npcId, Location loc) {
        return addSpawnWithoutRespawn(npcId, loc, 0);
    }

    public NpcInstance addSpawnWithoutRespawn(int npcId, Location loc, int randomOffset) {
        Location newLoc;
        if (randomOffset > 0) {
            newLoc = Location.findPointToStay(loc, 0, randomOffset, getGeoIndex()).setH(loc.h);
        } else {
            newLoc = loc;
        }

        return NpcUtils.spawnSingle(npcId, newLoc, this);
    }

    public void addSpawnWithRespawn(int npcId, Location loc, int randomOffset, int respawnDelay) {
        SimpleSpawner sp = new SimpleSpawner(npcId);
        sp.setLoc(randomOffset > 0 ? Location.findPointToStay(loc, 0, randomOffset, getGeoIndex()) : loc);
        sp.setReflection(this);
        sp.setAmount(1);
        sp.setRespawnDelay(respawnDelay);
        sp.doSpawn(true);
        sp.startRespawn();
    }

    public boolean isDefault() {
        return id <= 0;
    }

    public Set<Integer> getVisitors() {
        return visitors;
    }

    public void setReenterTime(long time) {
        Set<Integer> players;
        lock.lock();
        try {
            players = visitors;
        } finally {
            lock.unlock();
        }

        Player player;

        for (int objectId : players) {
            player = World.getPlayer(objectId);
            if (player != null) {
                player.setInstanceReuse(getInstancedZoneId(), time);
            } else {
                mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", objectId, getInstancedZoneId(), time);
            }

        }
    }

    protected void onCreate() {
        ReflectionManager.INSTANCE.add(this);
    }

    public void init(InstantZone instantZone) {
        setName(instantZone.getName());
        instance = instantZone;

        if (instantZone.getMapX() >= 0) {
            int geoIndex = GeoEngine.NextGeoIndex(instantZone.getMapX(), instantZone.getMapY(), id);
            setGeoIndex(geoIndex);
        }

        setTeleportLoc(instantZone.getTeleportCoord());
        if (instantZone.getReturnCoords() != null) {
            setReturnLoc(instantZone.getReturnCoords());
        }
        fillSpawns(instantZone.getSpawnsInfo());

        if (instantZone.getSpawns().size() > 0) {
            spawners = new HashMap<>(instantZone.getSpawns().size());
            for (Map.Entry<String, InstantZone.SpawnInfo2> entry : instantZone.getSpawns().entrySet()) {
                List<Spawner> spawnList = new ArrayList<>(entry.getValue().getTemplates().size());
                spawners.put(entry.getKey(), spawnList);

                for (SpawnTemplate template : entry.getValue().getTemplates()) {
                    HardSpawner spawner = new HardSpawner(template);
                    spawnList.add(spawner);

                    spawner.setAmount(template.getCount());
                    spawner.setRespawnDelay(template.getRespawn(), template.getRespawnRandom());
                    spawner.setReflection(this);
                    spawner.setRespawnTime(0);
                }

                if (entry.getValue().isSpawned()) {
                    spawnByGroup(entry.getKey());
                }
            }
        }

        init0(instantZone.getDoors(), instantZone.getZones());
        setCollapseIfEmptyTime(instantZone.getCollapseIfEmpty());
        startCollapseTimer(instantZone.getTimelimit() * 60 * 1000L);

        onCreate();
    }

    public void spawnByGroup(String name) {
        List<Spawner> list = spawners.get(name);
        if (list == null) {
            throw new IllegalArgumentException();
        }
        list.forEach(Spawner::init);
    }

    public void despawnByGroup(String name) {
        List<Spawner> list = spawners.get(name);
        if (list == null) {
            throw new IllegalArgumentException();
        }
        list.forEach(Spawner::deleteAll);
    }

    public Collection<Zone> getZones() {
        return zones.values();
    }

    public <T extends Listener> boolean addListener(T listener) {
        return listeners.add(listener);
    }

    public <T extends Listener> boolean removeListener(T listener) {
        return listeners.remove(listener);
    }

    private class ReflectionListenerList extends ListenerList {
        void onCollapse() {
            getListeners().stream()
                    .filter(l -> l instanceof OnReflectionCollapseListener)
                    .map(l -> (OnReflectionCollapseListener) l)
                    .forEach(l -> l.onReflectionCollapse(Reflection.this));
        }
    }
}