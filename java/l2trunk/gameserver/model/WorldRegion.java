package l2trunk.gameserver.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public final class WorldRegion /*implements Iterable<GameObject>*/ {

    final int x;
    final int y;
    final int z;

    private final AtomicBoolean isActive = new AtomicBoolean();

    private volatile List<GameObject> objects = new CopyOnWriteArrayList<>();
    /**
     * Зоны пересекающие этот регион
     */
    private volatile List<Zone> zones = new CopyOnWriteArrayList<>();
    private int playersCount = 0;
    /**
     * Запланированная задача активации/деактивации текущего и соседних регионов
     */
    private Future<?> activateTask;

    WorldRegion(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    void addToPlayers(GameObject object, Creature dropper) {
        if (object == null)
            return;

        Player player = null;
        if (object instanceof Player)
            player = (Player) object;

        int oid = object.objectId();
        int rid = object.getReflectionId();

        for (GameObject obj : objects) {
            if (obj.objectId() != oid && obj.getReflectionId() == rid) {
                // Если object - игрок, показать ему все видимые обьекты в регионе
                if (player != null)
                    player.sendPacket(player.addVisibleObject(obj, null));

                // Показать обьект всем игрокам в регионе
                if (obj instanceof Player) {
                    Player p = (Player) obj;
                    p.sendPacket(p.addVisibleObject(object, dropper));
                }
            }
        }
    }

    void removeFromPlayers(GameObject object) {
        if (object == null)
            return;

        Player player = (object instanceof Player) ? (Player) object : null;

        int oid = object.objectId();
        Reflection rid = object.getReflection();

        Player p;
        List<L2GameServerPacket> d = null;

        for (GameObject obj : objects) {
            if (obj.objectId() != oid && obj.getReflection() == rid) {// Если object - игрок, убрать у него все видимые обьекты в регионе
                if (player != null)
                    player.sendPacket(player.removeVisibleObject(obj, null));

                // Убрать обьект у всех игроков в регионе
                if (obj instanceof Player) {
                    p = (Player) obj;
                    p.sendPacket(p.removeVisibleObject(object, d == null ? d = object.deletePacketList() : d));
                }
            }

        }
    }

    public synchronized void addObject(GameObject obj) {
        if (obj == null)
            return;

        objects.add(obj);

        if (obj instanceof Player)
            if (playersCount++ == 0) {
                if (activateTask != null)
                    activateTask.cancel(false);
                //активируем регион и соседние регионы через секунду
                activateTask = ThreadPoolManager.INSTANCE.schedule(new ActivateTask(true), 1000L);
            }
    }

    public synchronized void removeObject(GameObject obj) {
        if (obj == null)
            return;

        objects.remove(obj);

        if (obj instanceof Player)
            if (--playersCount == 0) {
                if (activateTask != null)
                    activateTask.cancel(false);
                //деактивируем регион и соседние регионы через минуту
                activateTask = ThreadPoolManager.INSTANCE.schedule(new ActivateTask(false), 60000L);
            }
    }

    public boolean isEmpty() {
        return playersCount == 0;
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

        objects.stream()
                .filter(obj -> obj instanceof NpcInstance)
                .map(obj -> (NpcInstance) obj)
                .forEach(npc -> {
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
                });
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
        return "[" + x + ", " + y + ", " + z + "]";
    }

//    @Override
//    public Iterator<GameObject> iterator() {
//        return new InternalIterator(objects);
//    }


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

//    private class InternalIterator implements Iterator<GameObject> {
//        final GameObject[] objects;
//        int cursor = 0;
//
//        InternalIterator(final GameObject[] objects) {
//            this.objects = objects;
//        }
//
//        @Override
//        public boolean hasNext() {
//            if (cursor < objects.length)
//                return objects[cursor] != null;
//            return false;
//        }
//
//        @Override
//        public GameObject next() {
//            return objects[cursor++];
//        }
//
//        @Override
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//    }
}