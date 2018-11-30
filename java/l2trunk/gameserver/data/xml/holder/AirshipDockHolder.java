package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.templates.AirshipDock;

import java.util.HashMap;
import java.util.Map;

public final class AirshipDockHolder extends AbstractHolder {
    private static final AirshipDockHolder INSTANCE = new AirshipDockHolder();
    private final Map<Integer, AirshipDock> docks = new HashMap<>(4);

    public static AirshipDockHolder getInstance() {
        return INSTANCE;
    }

    public void addDock(AirshipDock dock) {
        docks.put(dock.getId(), dock);
    }

    public AirshipDock getDock(int dock) {
        return docks.get(dock);
    }

    @Override
    public int size() {
        return docks.size();
    }

    @Override
    public void clear() {
        docks.clear();
    }
}
