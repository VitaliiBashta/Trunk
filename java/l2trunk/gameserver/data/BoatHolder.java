package l2trunk.gameserver.data;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.templates.CharTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
        log();
        for (Boat boat : _boats.values()) {
            boat.spawnMe();
            info("Spawning: " + boat.getName());
        }
    }

    public Boat initBoat(String name, String clazz) {
        try {
            Class<?> cl = Class.forName("l2trunk.gameserver.model.entity.boat." + clazz);
            Constructor<?> constructor = cl.getConstructor(Integer.TYPE, CharTemplate.class);

            Boat boat = (Boat) constructor.newInstance(IdFactory.getInstance().getNextId(), TEMPLATE);
            boat.setName(name);
            addBoat(boat);
            return boat;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            error("Fail to init boat: " + clazz, e);
        }

        return null;
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
        _boats.put(boat.getObjectId(), boat);
    }

    public void removeBoat(Boat boat) {
        _boats.remove(boat.getObjectId());
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
