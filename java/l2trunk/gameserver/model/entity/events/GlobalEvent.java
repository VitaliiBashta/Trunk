package l2trunk.gameserver.model.entity.events;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.listener.Listener;
import l2trunk.commons.listener.ListenerList;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.dao.ItemsDAO;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.event.OnStartStopListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.DoorObject;
import l2trunk.gameserver.model.entity.events.objects.InitableObject;
import l2trunk.gameserver.model.entity.events.objects.SpawnableObject;
import l2trunk.gameserver.model.entity.events.objects.ZoneObject;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.taskmanager.actionrunner.ActionRunner;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public abstract class GlobalEvent {
    public static final Logger LOG = LoggerFactory.getLogger(GlobalEvent.class);
    protected static final String EVENT = "event";
    // actions
    protected final Map<Integer, List<EventAction>> onTimeActions = new TreeMap<>();
    private final List<EventAction> onStartActions = new ArrayList<>(0);
    private final List<EventAction> onStopActions = new ArrayList<>(0);
    private final List<EventAction> onInitActions = new ArrayList<>(0);
    // objects
    private final Map<String, List<Object>> _objects = new HashMap<>(0);
    private final int id;
    private final String _name;
    private final String _timerName;
    private final ListenerListImpl _listenerList = new ListenerListImpl();
    private Map<Integer, ItemInstance> _banishedItems = new HashMap<>();//Containers.emptyIntObjectMap();

    protected GlobalEvent(StatsSet set) {
        this(set.getInteger("id"), set.getString("name"));
    }

    protected GlobalEvent(int id, String name) {
        this.id = id;
        _name = name;
        _timerName = id + "_" + name.toLowerCase().replace(" ", "_");
    }

    protected static void broadcastToWorld(L2GameServerPacket packet) {
        GameObjectsStorage.getAllPlayersStream().forEach(player -> player.sendPacket(packet));
    }

    public void initEvent() {
        callActions(onInitActions);

        reCalcNextTime(true);

        printInfo();
    }

    public void startEvent() {
        callActions(onStartActions);

        _listenerList.onStart();
    }

    public void stopEvent() {
        callActions(onStopActions);

        _listenerList.onStop();
    }

    protected void printInfo() {
        LOG.info(getName() + " time - " + TimeUtils.toSimpleFormat(startTimeMillis()));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + getId() + ";" + getName() + "]";
    }

    // ===============================================================================================================
    // Actions
    // ===============================================================================================================

    private void callActions(List<EventAction> actions) {
        for (EventAction action : actions)
            action.call(this);
    }

    public void addOnStartActions(List<EventAction> start) {
        onStartActions.addAll(start);
    }

    public void addOnStopActions(List<EventAction> start) {
        onStopActions.addAll(start);
    }

    public void addOnInitActions(List<EventAction> start) {
        onInitActions.addAll(start);
    }

    public void addOnTimeAction(int time, EventAction action) {
        List<EventAction> list = onTimeActions.get(time);
        if (list != null)
            list.add(action);
        else {
            List<EventAction> actions = new ArrayList<>(1);
            actions.add(action);
            onTimeActions.put(time, actions);
        }
    }

    public void addOnTimeActions(int time, List<EventAction> actions) {
        if (actions.isEmpty())
            return;

        List<EventAction> list = onTimeActions.get(time);
        if (list != null)
            list.addAll(actions);
        else
            onTimeActions.put(time, new ArrayList<>(actions));
    }

    public void timeActions(int time) {
        List<EventAction> actions = onTimeActions.get(time);
        if (actions == null) {
            LOG.info("Undefined time : " + time);
            return;
        }

        callActions(actions);
    }

    public int[] timeActions() {
        return onTimeActions.keySet().stream().mapToInt(Number::intValue).toArray();
    }

    // ===============================================================================================================
    // Tasks
    // ===============================================================================================================

    public void registerActions() {
        long t = startTimeMillis();
        if (t == 0)
            return;

        for (int key : onTimeActions.keySet())
            ActionRunner.INSTANCE.register(t + key * 1000L, new EventWrapper(_timerName, this, key));
    }

    public void clearActions() {
        ActionRunner.INSTANCE.clear(_timerName);
    }

    // ===============================================================================================================
    // Objects
    // ===============================================================================================================

    @SuppressWarnings("unchecked")
    public <O> List<O> getObjects(String name) {
        List<Object> objects = _objects.get(name);
        return objects == null ? Collections.emptyList() : (List<O>) objects;
    }

    public <O extends Serializable> O getFirstObject(String name) {
        List<O> objects = getObjects(name);
        return objects.size() > 0 ? objects.get(0) : null;
    }

    public void addObject(String name, Object object) {
        if (object == null)
            return;

        List<Object> list = _objects.get(name);
        if (list != null) {
            list.add(object);
        } else {
            list = new CopyOnWriteArrayList<>();
            list.add(object);
            _objects.put(name, list);
        }
    }

    public void removeObject(String name, Serializable o) {
        if (o == null)
            return;

        List<Object> list = _objects.get(name);
        if (list != null)
            list.remove(o);
    }

    @SuppressWarnings("unchecked")
    public <O> List<O> removeObjects(String name) {
        List<Object> objects = _objects.remove(name);
        return objects == null ? Collections.emptyList() : (List<O>) objects;
    }

    @SuppressWarnings("unchecked")
    public void addObjects(String name, List objects) {
        if (objects.isEmpty())
            return;

        List<Object> list = _objects.get(name);
        if (list != null)
            list.addAll(objects);
        else
            _objects.put(name, objects);
    }

    public Map<String, List<Object>> getObjects() {
        return _objects;
    }

    public void spawnAction(String name, boolean spawn) {
        List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            LOG.info("Undefined objects: " + name);
            return;
        }

        for (Object object : objects)
            if (object instanceof SpawnableObject) {
                if (spawn)
                    ((SpawnableObject) object).spawnObject(this);
                else
                    ((SpawnableObject) object).despawnObject(this);
            }
    }

    public void doorAction(String name, boolean open) {
        List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            LOG.info("Undefined objects: " + name);
            return;
        }

        for (Object object : objects)
            if (object instanceof DoorObject) {
                if (open)
                    ((DoorObject) object).open(this);
                else
                    ((DoorObject) object).close(this);
            }
    }

    public void zoneAction(String name, boolean active) {
        List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            LOG.info("Undefined objects: " + name);
            return;
        }

        for (Object object : objects)
            if (object instanceof ZoneObject)
                ((ZoneObject) object).setActive(active, this);
    }

    public void initAction(String name) {
        List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            LOG.info("Undefined objects: " + name);
            return;
        }

        for (Object object : objects)
            if (object instanceof InitableObject)
                ((InitableObject) object).initObject(this);
    }

    public void action(String name, boolean start) {
        if (name.equalsIgnoreCase(EVENT)) {
            if (start)
                startEvent();
            else
                stopEvent();
        }
    }

    public void refreshAction(String name) {
        List<Serializable> objects = getObjects(name);
        if (objects.isEmpty()) {
            LOG.info("Undefined objects: " + name);
            return;
        }

        for (Object object : objects)
            if (object instanceof SpawnableObject)
                ((SpawnableObject) object).refreshObject(this);
    }

    // ===============================================================================================================
    // Abstracts
    // ===============================================================================================================

    protected abstract void reCalcNextTime(boolean onInit);

    protected abstract long startTimeMillis();

    protected void broadcastToWorld(IStaticPacket packet) {
        GameObjectsStorage.getAllPlayersStream().forEach(player -> player.sendPacket(packet));
    }

    // ===============================================================================================================
    // Getters & Setters
    // ===============================================================================================================
    public int getId() {
        return id;
    }

    public String getName() {
        return _name;
    }

    public GameObject getCenterObject() {
        return null;
    }

    public Reflection getReflection() {
        return ReflectionManager.DEFAULT;
    }

    public int getRelation(Player thisPlayer, Player target, int oldRelation) {
        return oldRelation;
    }

    public int getUserRelation(Player thisPlayer, int oldRelation) {
        return oldRelation;
    }

    public void checkRestartLocs(Player player, Map<RestartType, Boolean> r) {
        //
    }

    public Location getRestartLoc(Player player, RestartType type) {
        return null;
    }

    public boolean canAttack(Creature target, Creature attacker, Skill skill, boolean force) {
        return false;
    }

    public SystemMsg checkForAttack(Creature target, Creature attacker, Skill skill, boolean force) {
        return null;
    }

    public boolean canUseSkill(Creature caster, Creature target, Skill skill) {
        return true;
    }

    public boolean isInProgress() {
        return false;
    }

    public boolean isParticle(Player player) {
        return false;
    }

    public void announce(int a) {
        throw new UnsupportedOperationException();
    }

    public void teleportPlayers(String teleportWho) {
        throw new UnsupportedOperationException();
    }

    public boolean ifVar(String name) {
        throw new UnsupportedOperationException();
    }

    public Stream<Player> itemObtainPlayers() {
        throw new UnsupportedOperationException();
    }

    public void giveItem(Player player, int itemId, long count) {
        if (itemId == -300) {
            if (Config.ENABLE_ALT_FAME_REWARD) {
                if ((this instanceof CastleSiegeEvent))
                    count = Config.ALT_FAME_CASTLE;
                else if ((this instanceof FortressSiegeEvent))
                    count = Config.ALT_FAME_FORTRESS;
            }
            player.setFame(player.getFame() + (int) count, toString());
        } else {
            Functions.addItem(player, itemId, count, getName() + " Global Event");
        }
    }

    public Stream<Player> broadcastPlayers(int range) {
        throw new UnsupportedOperationException();
    }

    public boolean canRessurect(Player resurrectPlayer, Creature creature, boolean force) {
        return true;
    }

    // ===============================================================================================================
    // setEvent helper
    // ===============================================================================================================
    public void onAddEvent(GameObject o) {
        //
    }

    public void onRemoveEvent(GameObject o) {
        //
    }

    // ===============================================================================================================
    // Banish items
    // ===============================================================================================================
    protected void addBanishItem(ItemInstance item) {
        if (_banishedItems.isEmpty())
            _banishedItems = new HashMap<>();//CHashIntObjectMap<ItemInstance>();

        _banishedItems.put(item.getObjectId(), item);
    }

    public void removeBanishItems() {
        Iterator<Map.Entry<Integer, ItemInstance>> iterator = _banishedItems.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, ItemInstance> entry = iterator.next();
            iterator.remove();

            ItemInstance item = ItemsDAO.INSTANCE.load(entry.getKey());
            if (item != null) {
                if (item.getOwnerId() > 0) {
                    GameObject object = GameObjectsStorage.findObject(item.getOwnerId());
                    if (object != null && object.isPlayable()) {
                        if (object.isSummon())
                            ((Summon) object).getInventory().destroyItem(item, "removeBanishItems");
                        else
                            object.getPlayer().getInventory().destroyItem(item, "removeBanishItems");
                        object.getPlayer().sendPacket(SystemMessage2.removeItems(item));
                    }
                }
                item.delete();
            } else
                item = entry.getValue();

            item.deleteMe();
        }
    }

    // ===============================================================================================================
    // Listeners
    // ===============================================================================================================
    public void addListener(Listener l) {
        _listenerList.add(l);
    }

    public void removeListener(Listener l) {
        _listenerList.remove(l);
    }

    // ===============================================================================================================
    // Object
    // ===============================================================================================================
    protected void cloneTo(GlobalEvent e) {
        e.onInitActions.addAll(onInitActions);
        e.onStartActions.addAll(onStartActions);
        e.onStopActions.addAll(onStopActions);

        onTimeActions.forEach(e::addOnTimeActions);
    }

    private class ListenerListImpl extends ListenerList {
        void onStart() {
            getListeners().filter(l -> l instanceof OnStartStopListener)
                    .map(l -> (OnStartStopListener) l)
                    .forEach(l -> l.onStart(GlobalEvent.this));
        }

        void onStop() {
            getListeners().filter(l -> l instanceof OnStartStopListener)
                    .map(l -> (OnStartStopListener) l)
                    .forEach(l -> l.onStop(GlobalEvent.this));
        }
    }
}
