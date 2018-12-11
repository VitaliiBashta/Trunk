package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.ZoneTemplate;

import java.util.HashMap;
import java.util.Map;

public class ZoneHolder {
    private static final Map<String, ZoneTemplate> zones = new HashMap<>();

    public static void addTemplate(ZoneTemplate zone) {
        zones.put(zone.getName(), zone);
    }

    public static ZoneTemplate getTemplate(String name) {
        return zones.get(name);
    }

    public static Map<String, ZoneTemplate> getZones() {
        return zones;
    }

    public static int size() {
        return zones.size();
    }

    public void clear() {
        zones.clear();
    }
}
