package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.residence.*;
import l2trunk.gameserver.utils.Location;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ResidenceHolder {
    private final static Map<Integer, Residence> RESIDENCES = new TreeMap<>();

    private ResidenceHolder() {
    }

    public static void add(Residence r) {
        RESIDENCES.put(r.getId(), r);
    }

    public static Residence getResidence(int id) {
        return RESIDENCES.get(id);
    }

    public static ClanHall getClanHall(int id) {
        return getClanHalls().stream()
                .filter(r -> r.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(" no clanhall with id " + id));

    }

    public static Castle getCastle(int id) {
        return getCastles().stream()
                .filter(r -> r.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(" no catle with id " + id));
    }

    public static Fortress getFortress(int id) {
        return getFortresses()
                .filter(c -> c.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(" no fortress with id " + id));

    }

    public static Dominion getDominion(int id) {
        return getDominions().stream()
                .filter(c -> c.getId() == id)
                .findFirst().orElseThrow(() -> new IllegalArgumentException(" no dominion with id " + id));
    }


    private static <R extends Residence> List<R> getResidenceList(Class<R> t) {
        return RESIDENCES.values().stream()
                .filter(r -> r.getClass().isAssignableFrom(t)) //TODO check what is inheritance
                .map(t::cast)
                .collect(Collectors.toList());
    }

    public static List<Castle> getCastles() {
        return RESIDENCES.values().stream()
                .filter(r -> r instanceof Castle)
                .map(r -> (Castle) r)
                .collect(Collectors.toList());
    }

    public static List<ClanHall> getClanHalls() {
        return RESIDENCES.values().stream()
                .filter(r -> r instanceof ClanHall)
                .map(r -> (ClanHall) r)
                .collect(Collectors.toList());
    }

    public static Stream<Fortress> getFortresses() {
        return RESIDENCES.values().stream()
                .filter(r -> r instanceof Fortress)
                .map(r -> (Fortress) r);
    }

    public static List<Dominion> getDominions() {
        return RESIDENCES.values().stream()
                .filter(r -> r instanceof Dominion)
                .map(r -> (Dominion) r)
                .collect(Collectors.toList());
    }

    public static Collection<Residence> getResidences() {
        return RESIDENCES.values();
    }

    public static <R extends Residence> R getResidenceByObject(Class<R> type, GameObject object) {
        return getResidenceByCoord(type, object.getLoc(), object.getReflection());
    }

    private static <R extends Residence> R getResidenceByCoord(Class<R> type, Location loc, Reflection ref) {
        return getResidenceList(type).stream()
                .filter(residence -> residence.checkIfInZone(loc, ref))
                .findFirst().orElse(null);
    }

    public static <R extends Residence> R findNearestResidence(Class<R> clazz, Location loc, Reflection ref, int offset) {
        R residence = getResidenceByCoord(clazz, loc, ref);
        if (residence == null) {
            double closestDistance = offset;
            double distance;
            for (R r : getResidenceList(clazz)) {
                distance = r.getZone().findDistanceToZone(loc, false);
                if (closestDistance > distance) {
                    closestDistance = distance;
                    residence = r;
                }
            }
        }
        return residence;
    }

    public static void init() {
        RESIDENCES.values().forEach(Residence::init);
    }

    public static int size() {
        return RESIDENCES.size();
    }

}
