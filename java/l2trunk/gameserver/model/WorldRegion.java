package l2trunk.gameserver.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WorldRegion implements Iterable<GameObject> {

    @SuppressWarnings("unused")
    private static final Logger _log = LoggerFactory.getLogger(WorldRegion.class);
    /**
     * Координаты региона в мире
     */
    private final int tileX, tileY, tileZ;

    private final AtomicBoolean isActive = new AtomicBoolean();
    /**
     * Все объекты в регионе
     */
    private volatile GameObject[] _objects = new GameObject[0];
    /**
     * Количество объектов в регионе
     */
    private int _objectsCount = 0;
    /**
     * Зоны пересекающие этот регион
     */
    private volatile List<Zone> zones = new CopyOnWriteArrayList<>();
    /**
     * Количество игроков в регионе
     */
    private int _playersCount = 0;
    /**
     * Запланированная задача активации/деактивации текущего и соседних регионов
     */
    private Future<?> _activateTask;

    WorldRegion(int x, int y, int z) {
        tileX = x;
        tileY = y;
        tileZ = z;
    }

    int getX() {
        return tileX;
    }

    int getY() {
        return tileY;
    }

    int getZ() {
        return tileZ;
    }

    void addToPlayers(GameObject object, Creature dropper) {
        if (object == null)
            return;

        Player player = null;
        if (object.isPlayer())
            player = (Player) object;

        int oid = object.getObjectId();
        int rid = object.getReflectionId();

        Player p;

        for (GameObject obj : this) {
            if (obj.getObjectId() == oid || obj.getReflectionId() != rid)
                continue;
            // Если object - игрок, показать ему все видимые обьекты в регионе
            if (player != null)
                player.sendPacket(player.addVisibleObject(obj, null));

            // Показать обьект всем игрокам в регионе
            if (obj.isPlayer()) {
                p = (Player) obj;
                p.sendPacket(p.addVisibleObject(object, dropper));
            }
        }
    }

    void removeFromPlayers(GameObject object) {
        if (object == null)
            return;

        Player player = null;
        if (object.isPlayer())
            player = (Player) object;

        int oid = object.getObjectId();
        Reflection rid = object.getReflection();

        Player p;
        List<L2GameServerPacket> d = null;

        for (GameObject obj : this) {
            if (obj.getObjectId() == oid || obj.getReflection() != rid)
                continue;

            // Если object - игрок, убрать у него все видимые обьекты в регионе
            if (player != null)
                player.sendPacket(player.removeVisibleObject(obj, null));

            // Убрать обьект у всех игроков в регионе
            if (obj.isPlayer()) {
                p = (Player) obj;
                p.sendPacket(p.removeVisibleObject(object, d == null ? d = object.deletePacketList() : d));
            }
        }
    }

    public synchronized void addObject(GameObject obj) {
        if (obj == null)
            return;

        GameObject[] objects = _objects;

        GameObject[] resizedObjects = new GameObject[_objectsCount + 1];
        System.arraycopy(objects, 0, resizedObjects, 0, _objectsCount);
        objects = resizedObjects;
        objects[_objectsCount++] = obj;

        _objects = resizedObjects;

        if (obj.isPlayer())
            if (_playersCount++ == 0) {
                if (_activateTask != null)
                    _activateTask.cancel(false);
                //активируем регион и соседние регионы через секунду
                _activateTask = ThreadPoolManager.INSTANCE.schedule(new ActivateTask(true), 1000L);
            }
    }

    public synchronized void removeObject(GameObject obj) {
        if (obj == null)
            return;

        GameObject[] objects = _objects;

        int index = -1;

        for (int i = 0; i < _objectsCount; i++) {
            if (objects[i] == obj) {
                index = i;
                break;
            }
        }

        if (index == -1) //Ошибочная ситуация
            return;

        _objectsCount--;

        GameObject[] resizedObjects = new GameObject[_objectsCount];
        objects[index] = objects[_objectsCount];
        System.arraycopy(objects, 0, resizedObjects, 0, _objectsCount);

        _objects = resizedObjects;

        if (obj.isPlayer())
            if (--_playersCount == 0) {
                if (_activateTask != null)
                    _activateTask.cancel(false);
                //деактивируем регион и соседние регионы через минуту
                _activateTask = ThreadPoolManager.INSTANCE.schedule(new ActivateTask(false), 60000L);
            }
    }

    public boolean isEmpty() {
        return _playersCount == 0;
    }

    public boolean isActive() {
        return isActive.get();
    }

    /**
     * Активация региона, включить или выключить AI всех NPC в регионе
     *
     * @param activate - переключатель
     */
    void setActive(boolean activate) {
        if (!isActive.compareAndSet(!activate, activate))
            return;

        NpcInstance npc;
        for (GameObject obj : this) {
            if (!obj.isNpc())
                continue;
            npc = (NpcInstance) obj;
            if (npc.getAI().isActive() != isActive())
                if (isActive()) {
                    npc.getAI().startAITask();
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                    npc.startRandomAnimation();
                } else if (!npc.getAI().isGlobalAI()) {
                    npc.getAI().stopAITask();
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
                    npc.stopRandomAnimation();
                }
        }
    }

    synchronized void addZone(Zone zone) {
        zones.add(zone);
    }

    synchronized void removeZone(Zone zone) {
        zones.remove(zone);
    }

    List<Zone> getZones() {
        // Без синхронизации и копирования, т.к. удаление/добавление зон происходит достаточно редко
        return zones;
    }

    @Override
    public String toString() {
        return "[" + tileX + ", " + tileY + ", " + tileZ + "]";
    }

    @Override
    public Iterator<GameObject> iterator() {
        return new InternalIterator(_objects);
    }


    public class ActivateTask extends RunnableImpl {
        private final boolean isActivating;

        ActivateTask(boolean isActivating) {
            this.isActivating = isActivating;
        }

        @Override
        public void runImpl() {
            if (isActivating)
                World.activate(WorldRegion.this);
            else
                World.deactivate(WorldRegion.this);
        }
    }

    private class InternalIterator implements Iterator<GameObject> {
        final GameObject[] objects;
        int cursor = 0;

        InternalIterator(final GameObject[] objects) {
            this.objects = objects;
        }

        @Override
        public boolean hasNext() {
            if (cursor < objects.length)
                return objects[cursor] != null;
            return false;
        }

        @Override
        public GameObject next() {
            return objects[cursor++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}