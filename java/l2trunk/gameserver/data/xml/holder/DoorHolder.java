package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.DoorTemplate;

import java.util.HashMap;
import java.util.Map;


public final class DoorHolder {
    private static final Map<Integer, DoorTemplate> doors = new HashMap<>();

    private DoorHolder() {
    }

    public static void addTemplate(DoorTemplate door) {
        doors.put(door.getNpcId(), door);
    }

    public static DoorTemplate getTemplate(int doorId) {
        return doors.get(doorId);
    }

    public static Map<Integer, DoorTemplate> getDoors() {
        return doors;
    }

    public static int size() {
        return doors.size();
    }

}
