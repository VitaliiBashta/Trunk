package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.DressCloakData;

import java.util.ArrayList;
import java.util.List;

public final class DressCloakHolder {
    private static final List<DressCloakData> cloaks = new ArrayList<>();

    private DressCloakHolder() {
    }

    public static void addCloak(DressCloakData cloak) {
        cloaks.add(cloak);
    }

    public static int size() {
        return cloaks.size();
    }

    public static List<DressCloakData> getAllCloaks() {
        return cloaks;
    }

    public DressCloakData getCloak(int id) {
        return cloaks.stream()
                .filter(cloak -> cloak.getId() == id)
                .findFirst().orElse(null);
    }

    public static void clear() {
        cloaks.clear();
    }
}
