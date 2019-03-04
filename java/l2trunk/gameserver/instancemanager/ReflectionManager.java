package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.data.xml.holder.DoorHolder;
import l2trunk.gameserver.data.xml.holder.ZoneHolder;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.utils.Location;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum ReflectionManager {
    INSTANCE;
    public static final Reflection DEFAULT = Reflection.createReflection(0);
    public static final Reflection PARNASSUS = Reflection.createReflection(-1);
    public static final Reflection GIRAN_HARBOR = Reflection.createReflection(-2);
    public static final Reflection JAIL = Reflection.createReflection(-3);
    private static final Reflection CTF_EVENT = Reflection.createReflection(-4);
    private static final Reflection TVT_EVENT = Reflection.createReflection(-5);
    private final Map<Integer, Reflection> reflections = new HashMap<>();

    public void init() {
        add(DEFAULT);
        add(PARNASSUS);
        add(GIRAN_HARBOR);
        add(JAIL);
        add(CTF_EVENT);
        add(TVT_EVENT);

        // создаем в рефлекте все зоны, и все двери
        DEFAULT.init(DoorHolder.getDoors(), ZoneHolder.getZones());

        JAIL.setCoreLoc(Location.of(-114648, -249384, -2984));
    }

    public synchronized Reflection get(int id) {
        return reflections.get(id);
    }

    public synchronized Reflection add(Reflection ref) {
        return reflections.put(ref.id, ref);
    }

    public synchronized Reflection remove(Reflection ref) {
        return reflections.remove(ref.id);
    }

    public synchronized Collection<Reflection> getAll() {
        return reflections.values();
    }

    public synchronized long getCountByIzId(int izId) {
        return reflections.values().stream().filter(a -> a.getInstancedZoneId() == izId).count();
    }

    public int size() {
        return reflections.size();
    }
}