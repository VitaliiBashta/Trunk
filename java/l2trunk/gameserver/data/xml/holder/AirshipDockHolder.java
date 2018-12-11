package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.AirshipDock;

import java.util.HashMap;
import java.util.Map;

public final class AirshipDockHolder {
    private final static Map<Integer, AirshipDock> docks = new HashMap<>(4);

    private AirshipDockHolder() {
    }

    public static void addDock(AirshipDock dock) {
        docks.put(dock.getId(), dock);
    }

    public static AirshipDock getDock(int dockId) {
        return docks.get(dockId);
    }

    public static int size() {
        return docks.size();
    }

    public static void clear() {
        docks.clear();
    }
}
