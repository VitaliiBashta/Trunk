package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.residence.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class ResidenceHolder {
    private ResidenceHolder() {
    }

    private final static Map<Integer, Residence> RESIDENCES = new TreeMap<>();

    private static final Map<Class, List<Residence>> _fastResidencesByType = new HashMap<>(4);

    public static void addResidence(Residence r) {
        RESIDENCES.put(r.getId(), r);
    }

    public static <R extends Residence> R getResidence(int id) {
        Residence residence = RESIDENCES.get(id);
        return (R) residence;
    }

    public static <R extends Residence> R getResidence(Class<R> type, int id) {
        Residence r = getResidence(id);
        if (r == null || r.getClass() != type)
            throw new IllegalArgumentException("Can't find residence with id " + id);

        return (R) r;
    }

    public static <R extends Residence> List<R> getResidenceList(Class<R> t) {
        return (List<R>) _fastResidencesByType.get(t);
    }

    public static Collection<Residence> getResidences() {
        return RESIDENCES.values();
    }

    public static <R extends Residence> R getResidenceByObject(Class<? extends Residence> type, GameObject object) {
        return (R) getResidenceByCoord(type, object.getX(), object.getY(), object.getZ(), object.getReflection());
    }

    private static <R extends Residence> R getResidenceByCoord(Class<R> type, int x, int y, int z, Reflection ref) {
        Collection<Residence> residences = type == null ? RESIDENCES.values() : (Collection<Residence>) getResidenceList(type);
        for (Residence residence : residences) {
            if (residence.checkIfInZone(x, y, z, ref))
                return (R) residence;
        }
        return null;
    }

    public static <R extends Residence> R findNearestResidence(Class<R> clazz, int x, int y, int z, Reflection ref, int offset) {
        Residence residence = getResidenceByCoord(clazz, x, y, z, ref);
        if (residence == null) {
            double closestDistance = offset;
            double distance;
            for (Residence r : getResidenceList(clazz)) {
                distance = r.getZone().findDistanceToZone(x, y, z, false);
                if (closestDistance > distance) {
                    closestDistance = distance;
                    residence = r;
                }
            }
        }
        return (R) residence;
    }

    public static void callInit() {
        RESIDENCES.values().forEach(Residence::init);
    }

    public static void buildFastLook() {
        for (Residence residence : RESIDENCES.values()) {
            List<Residence> list = _fastResidencesByType.computeIfAbsent(residence.getClass(), k -> new ArrayList<>());
            list.add(residence);
        }
    }

    public static int size() {
        return RESIDENCES.size();
    }

    public void clear() {
        RESIDENCES.clear();
        _fastResidencesByType.clear();
    }
}
