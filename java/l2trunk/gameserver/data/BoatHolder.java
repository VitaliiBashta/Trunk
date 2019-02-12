package l2trunk.gameserver.data;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.entity.boat.AirShip;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.model.entity.boat.Vehicle;
import l2trunk.gameserver.templates.CharTemplate;

import java.util.HashMap;
import java.util.Map;

public final class BoatHolder extends AbstractHolder {
    public static final CharTemplate TEMPLATE = new CharTemplate(CharTemplate.getEmptyStatsSet());

    private static final BoatHolder _instance = new BoatHolder();
    private final Map<Integer, Boat> _boats = new HashMap<>();

    public static BoatHolder getInstance() {
        return _instance;
    }

    public void spawnAll() {
        LOG.info(String.format("loaded %d %s(s) count.", _boats.size(), getClass().getSimpleName()));

        for (Boat boat : _boats.values()) {
            boat.spawnMe();
            LOG.info("Spawning: " + boat.getName());
        }
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
        return _boats.entrySet().stream()
                .filter(entry -> entry.getValue().getName().equals(name))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    public Boat getBoat(int objectId) {
        return _boats.get(objectId);
    }

    public void addBoat(Boat boat) {
        _boats.put(boat.objectId(), boat);
    }

    public void removeBoat(Boat boat) {
        _boats.remove(boat.objectId());
    }

    @Override
    public int size() {
        return _boats.size();
    }

    @Override
    public void clear() {
        _boats.clear();
    }
}
