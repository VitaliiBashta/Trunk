package l2f.gameserver.data.xml.holder;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.templates.DoorTemplate;

import java.util.HashMap;
import java.util.Map;


public final class DoorHolder extends AbstractHolder {
    private static final DoorHolder _instance = new DoorHolder();

    private Map<Integer,DoorTemplate> doors = new HashMap<>();

    public static DoorHolder getInstance() {
        return _instance;
    }

    public void addTemplate(DoorTemplate door) {
        doors.put(door.getNpcId(), door);
    }

    public DoorTemplate getTemplate(int doorId) {
        return doors.get(doorId);
    }

    public Map<Integer,DoorTemplate> getDoors() {
        return doors;
    }

    @Override
    public int size() {
        return doors.size();
    }

    @Override
    public void clear() {
        doors.clear();
    }
}
