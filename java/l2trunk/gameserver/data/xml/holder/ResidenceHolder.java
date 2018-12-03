package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.residence.Residence;

import java.util.*;

public final class ResidenceHolder extends AbstractHolder {
    private static final ResidenceHolder _instance = new ResidenceHolder();

    private final Map<Integer, Residence> _residences = new TreeMap<>();

    private final Map<Class, List<Residence>> _fastResidencesByType = new HashMap<>(4);

    private ResidenceHolder() {
        //
    }

    public static ResidenceHolder getInstance() {
        return _instance;
    }

    public void addResidence(Residence r) {
        _residences.put(r.getId(), r);
    }

    public <R extends Residence> R getResidence(int id) {
        return (R) _residences.get(id);
    }

    public <R extends Residence> R getResidence(Class<R> type, int id) {
        Residence r = getResidence(id);
        if (r == null || r.getClass() != type)
            return null;

        return (R) r;
    }

    public <R extends Residence> List<R> getResidenceList(Class<R> t) {
        return (List<R>) _fastResidencesByType.get(t);
    }

    public Collection<Residence> getResidences() {
        return _residences.values();
    }

    public <R extends Residence> R getResidenceByObject(Class<? extends Residence> type, GameObject object) {
        return (R) getResidenceByCoord(type, object.getX(), object.getY(), object.getZ(), object.getReflection());
    }

    private <R extends Residence> R getResidenceByCoord(Class<R> type, int x, int y, int z, Reflection ref) {
        Collection<Residence> residences = type == null ? getResidences() : (Collection<Residence>) getResidenceList(type);
        for (Residence residence : residences) {
            if (residence.checkIfInZone(x, y, z, ref))
                return (R) residence;
        }
        return null;
    }

    public <R extends Residence> R findNearestResidence(Class<R> clazz, int x, int y, int z, Reflection ref, int offset) {
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

    public void callInit() {
        for (Residence r : getResidences())
            r.init();
    }

    public void buildFastLook() {
        for (Residence residence : _residences.values()) {
            List<Residence> list = _fastResidencesByType.computeIfAbsent(residence.getClass(), k -> new ArrayList<>());
            list.add(residence);
        }
        LOG.info("total size: " + _residences.size());
        for (Map.Entry<Class, List<Residence>> entry : _fastResidencesByType.entrySet())
            LOG.info(" - load " + entry.getValue().size() + " " + entry.getKey().getSimpleName().toLowerCase() + "(s).");

    }

    @Override
    public int size() {
        return _residences.size();
    }

    @Override
    public void clear() {
        _residences.clear();
        _fastResidencesByType.clear();
    }
}
