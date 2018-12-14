package l2trunk.gameserver.model.entity;

import l2trunk.commons.listener.Listener;
import l2trunk.commons.listener.ListenerList;
import l2trunk.commons.threading.RunnableImpl;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Reflection {
    private static final Logger _log = LoggerFactory.getLogger(Reflection.class);
    private final static AtomicInteger _nextId = new AtomicInteger();
    protected final Lock lock = new ReentrantLock();
    protected final List<GameObject> objects = new CopyOnWriteArrayList<>();
    private final int id;
    private final ReflectionListenerList listeners = new ReflectionListenerList();
    private final List<Spawner> _spawns = new ArrayList<>();
    private final Set<Integer> _visitors = new HashSet<>();
    int _playerCount;
    // vars
    private Map<Integer, DoorInstance> doors = new HashMap<>();
    private Map<String, Zone> zones = Collections.emptyMap();
    private Map<String, List<Spawner>> _spawners = Collections.emptyMap();
    private Party party;
    private CommandChannel _commandChannel;
    private String name = "";
    private InstantZone instance;
    private int geoIndex;
    private Location _resetLoc; // место, к которому кидает при использовании SoE/unstuck, иначе выбрасывает в основной мир
    private Location _returnLoc; // если не прописано reset, но прописан return, то телепортит туда, одновременно перемещая в основной мир
    private Location teleportLoc; // точка входа
    private int _collapseIfEmptyTime;

    private boolean _isCollapseStarted;
    private Future<?> _collapseTask;
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

    public int getId() {
        return id;
    }

    public int getInstancedZoneId() {
        return instance == null ? 0 : instance.getId();
    }

    protected Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public void setCommandChannel(CommandChannel commandChannel) {
        _commandChannel = commandChannel;
    }

    private void setCollapseIfEmptyTime(int value) {
        _collapseIfEmptyTime = value;
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public InstantZone getInstancedZone() {
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
        return _resetLoc;
    }

    public void setCoreLoc(Location l) {
        _resetLoc = l;
    }

    public Location getReturnLoc() {
        return _returnLoc;
    }

    public void setReturnLoc(Location l) {
        _returnLoc = l;
    }

    public Location getTeleportLoc() {
        return teleportLoc;
    }

    void setTeleportLoc(Location l) {
        teleportLoc = l;
    }

    public List<Spawner> getSpawns() {
        return _spawns;
    }

    public Collection<DoorInstance> getDoors() {
        return doors.values();
    }

    public DoorInstance getDoor(int id) {
        return doors.get(id);
    }

    public Zone getZone(String name) {
        if (!zones.containsKey(name)) {
            _log.warn("not found zone for name1: " + name);
//            System.exit(1);
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
            if (_collapseTask != null) {
                _collapseTask.cancel(false);
                _collapseTask = null;
            }
            if (_collapse1minTask != null) {
                _collapse1minTask.cancel(false);
                _collapse1minTask = null;
            }
            _collapseTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                @Override
                public void runImpl() {
                    collapse();
                }
            }, timeInMillis);

            if (timeInMillis >= (60 * 1000L)) {
                _collapse1minTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        minuteBeforeCollapse();
                    }
                }, timeInMillis - (60 * 1000L));
            }
        } finally {
            lock.unlock();
        }
    }

    private void stopCollapseTimer() {
        lock.lock();
        try {
            if (_collapseTask != null) {
                _collapseTask.cancel(false);
                _collapseTask = null;
            }

            if (_collapse1minTask != null) {
                _collapse1minTask.cancel(false);
                _collapse1minTask = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private void minuteBeforeCollapse() {
        if (_isCollapseStarted) {
            return;
        }
        lock.lock();
        try {
            for (GameObject o : objects) {
                if (o.isPlayer()) {
                    Player player = (Player) o;
                    if (player.isInParty())
                        player.updatePartyInstance();
                    else
                        player.updateSoloInstance();

                    player.sendPacket(new SystemMessage(SystemMessage.THIS_INSTANCE_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTES_YOU_WILL_BE_FORCED_OUT_OF_THE_DANGEON_THEN_TIME_EXPIRES).addNumber(1));
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void collapse() {
        if (id <= 0) {
            new Exception("Basic reflection " + id + " could not be collapsed!").printStackTrace();
            return;
        }

        lock.lock();
        try {
            if (_isCollapseStarted) {
                return;
            }

            _isCollapseStarted = true;
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

            for (Spawner s : _spawns) {
                s.deleteAll();
            }

            for (String group : _spawners.keySet()) {
                despawnByGroup(group);
            }

            for (DoorInstance d : doors.values()) {
                d.deleteMe();
            }
            doors.clear();

            for (Zone zone : zones.values()) {
                zone.setActive(false);
            }
            zones.clear();

            List<Player> teleport = new ArrayList<>();
            List<GameObject> delete = new ArrayList<>();

            lock.lock();
            try {
                for (GameObject o : objects) {
                    if (o.isPlayer()) {
                        teleport.add((Player) o);
                    } else if (!o.isPlayable()) {
                        delete.add(o);
                    }
                }
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

            for (GameObject o : delete) {
                o.deleteMe();
            }

            _spawns.clear();
            objects.clear();
            _visitors.clear();
            doors.clear();

            _playerCount = 0;

            onCollapse();
        } finally {
            ReflectionManager.INSTANCE.remove(this);
            GeoEngine.FreeGeoIndex(getGeoIndex());
        }
    }

    protected void onCollapse() {
    }

    public void addObject(GameObject o) {
        if (_isCollapseStarted) {
            return;
        }

        lock.lock();
        try {
            objects.add(o);
            if (o.isPlayer()) {
                _playerCount++;
                _visitors.add(o.getObjectId());
                onPlayerEnter(o.getPlayer());
            }
        } finally {
            lock.unlock();
        }
        if ((_collapseIfEmptyTime > 0) && (_hiddencollapseTask != null)) {
            _hiddencollapseTask.cancel(false);
            _hiddencollapseTask = null;
        }
    }

    public void removeObject(GameObject o) {
        if (_isCollapseStarted) {
            return;
        }

        lock.lock();
        try {
            if (!objects.remove(o)) {
                return;
            }
            if (o.isPlayer()) {
                _playerCount--;
                onPlayerExit(o.getPlayer());
            }
        } finally {
            lock.unlock();
        }

        if ((_playerCount <= 0) && !isDefault() && (_hiddencollapseTask == null)) {
            if (_collapseIfEmptyTime <= 0) {
                collapse();
            } else {
                _hiddencollapseTask = ThreadPoolManager.INSTANCE.schedule(this::collapse, _collapseIfEmptyTime * 60 * 1000L);
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

    public synchronized List<Player> getPlayers() {
        return objects.stream()
                .filter(GameObject::isPlayer)
                .map(o -> (Player) o)
                .collect(Collectors.toList());
    }

    public synchronized List<NpcInstance> getNpcs() {
        lock.lock();
        try {
            return objects.stream()
                    .filter(GameObject::isNpc)
                    .map(o -> (NpcInstance) o)
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    public List<NpcInstance> getAllByNpcId(int npcId, boolean onlyAlive) {
        return objects.stream()
                .filter(GameObject::isNpc)
                .map(o -> (NpcInstance) o)
                .filter(npc -> npcId == npc.getNpcId())
                .filter(npc -> (!onlyAlive || !npc.isDead()))
                .collect(Collectors.toList());
    }

    public boolean canChampions() {
        return id <= 0;
    }

    public boolean isAutolootForced() {
        return false;
    }

    public boolean isCollapseStarted() {
        return _isCollapseStarted;
    }

    protected void addSpawn(SimpleSpawner spawn) {
        if (spawn != null) {
            _spawns.add(spawn);
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
                    c.setLoc(s.getCoords().get(Rnd.get(s.getCoords().size())));
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
                    c.setTerritory(s.getLoc());
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
        for (DoorInstance door : doors.values()) {
            if (door.getTemplate().getMasterDoor() > 0) {
                DoorInstance masterDoor = getDoor(door.getTemplate().getMasterDoor());

                masterDoor.addListener(new MasterOnOpenCloseListenerImpl(door));
            }
        }
    }

    /**
     * Открывает дверь в отражении
     */
    public void openDoor(int doorId) {
        DoorInstance door = doors.get(doorId);
        if (door != null) {
            door.openMe();
        }
    }

    /**
     * Закрывает дверь в отражении
     */
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

        for (NpcInstance n : getNpcs()) {
            n.deleteMe();
        }

        startCollapseTimer(timeInMinutes * 60 * 1000L);

        if (message) {
            for (Player pl : getPlayers()) {
                if (pl != null) {
                    pl.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(timeInMinutes));
                }
            }
        }
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

    public NpcInstance addSpawnWithRespawn(int npcId, Location loc, int randomOffset, int respawnDelay) {
        SimpleSpawner sp = new SimpleSpawner(npcId);
        sp.setLoc(randomOffset > 0 ? Location.findPointToStay(loc, 0, randomOffset, getGeoIndex()) : loc);
        sp.setReflection(this);
        sp.setAmount(1);
        sp.setRespawnDelay(respawnDelay);
        sp.doSpawn(true);
        sp.startRespawn();
        return sp.getLastSpawn();
    }

    public boolean isDefault() {
        return getId() <= 0;
    }

    public Set<Integer> getVisitors() {
        return _visitors;
    }

    public void removeVisitor(int objectId) {
        _visitors.remove(objectId);
    }

    public void setReenterTime(long time) {
        Set<Integer> players;
        lock.lock();
        try {
            players = _visitors;
        } finally {
            lock.unlock();
        }

        if (players != null) {
            Player player;

            for (int objectId : players) {
                try {
                    player = World.getPlayer(objectId);
                    if (player != null) {
                        player.setInstanceReuse(getInstancedZoneId(), time);
                    } else {
                        mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", objectId, getInstancedZoneId(), time);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            int geoIndex = GeoEngine.NextGeoIndex(instantZone.getMapX(), instantZone.getMapY(), getId());
            setGeoIndex(geoIndex);
        }

        setTeleportLoc(instantZone.getTeleportCoord());
        if (instantZone.getReturnCoords() != null) {
            setReturnLoc(instantZone.getReturnCoords());
        }
        fillSpawns(instantZone.getSpawnsInfo());

        if (instantZone.getSpawns().size() > 0) {
            _spawners = new HashMap<>(instantZone.getSpawns().size());
            for (Map.Entry<String, InstantZone.SpawnInfo2> entry : instantZone.getSpawns().entrySet()) {
                List<Spawner> spawnList = new ArrayList<>(entry.getValue().getTemplates().size());
                _spawners.put(entry.getKey(), spawnList);

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
        List<Spawner> list = _spawners.get(name);
        if (list == null) {
            throw new IllegalArgumentException();
        }

        for (Spawner s : list) {
            s.init();
        }
    }

    public void despawnByGroup(String name) {
        List<Spawner> list = _spawners.get(name);
        if (list == null) {
            throw new IllegalArgumentException();
        }

        for (Spawner s : list) {
            s.deleteAll();
        }
    }

    public Collection<Zone> getZones() {
        return zones.values();
    }

    public <T extends Listener<Reflection>> boolean addListener(T listener) {
        return listeners.add(listener);
    }

    public <T extends Listener<Reflection>> boolean removeListener(T listener) {
        return listeners.remove(listener);
    }

    class ReflectionListenerList extends ListenerList<Reflection> {
        void onCollapse() {
            if (!getListeners().isEmpty()) {
                for (Listener<Reflection> listener : getListeners()) {
                    ((OnReflectionCollapseListener) listener).onReflectionCollapse(Reflection.this);
                }
            }
        }
    }
}