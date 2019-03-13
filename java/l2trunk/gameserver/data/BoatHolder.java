package l2trunk.gameserver.data;

import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.entity.boat.AirShip;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.model.entity.boat.Vehicle;
import l2trunk.gameserver.templates.CharTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class BoatHolder {
    public static final CharTemplate TEMPLATE = new CharTemplate(CharTemplate.getEmptyStatsSet());
    public static final Logger LOG = LoggerFactory.getLogger(BoatHolder.class);
    private static final BoatHolder _instance = new BoatHolder();
    private final Map<Integer, Boat> boats = new HashMap<>();

    public static BoatHolder getInstance() {
        return _instance;
    }

    public void spawnAll() {
        LOG.info(String.format("loaded %d %s(s) count.", boats.size(), getClass().getSimpleName()));

        boats.values().forEach(boat -> {
            boat.spawnMe();
            LOG.info("Spawning: " + boat.getName());
        });
    }

    private Boat getBoatByname(String className, int id) {
        switch (className) {
            case "AirShip":
                return new AirShip(id, TEMPLATE);
            case "Vehicle":
                return new Vehicle(id, TEMPLATE);
            default:
                throw new IllegalArgumentException("no boat for name: " + className);
        }
    }

    public Boat initBoat(String name, String clazz) {
        Boat boat = getBoatByname(clazz, IdFactory.getInstance().getNextId());
        boat.setName(name);
        addBoat(boat);
        return boat;
    }

    public Boat getBoat(String name) {
        return boats.entrySet().stream()
                .filter(entry -> entry.getValue().getName().equals(name))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public Boat getBoat(int objectId) {
        return boats.get(objectId);
    }

    public void addBoat(Boat boat) {
        boats.put(boat.objectId(), boat);
    }

    public void removeBoat(Boat boat) {
        boats.remove(boat.objectId());
    }

    public int size() {
        return boats.size();
    }

    public void clear() {
        boats.clear();
    }
}
