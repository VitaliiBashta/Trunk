package l2trunk.gameserver.data.xml.holder;

import java.util.HashMap;
import java.util.Map;

public final class FoundationHolder {
    private static final Map<Integer, Integer> foundations = new HashMap<>();

    private FoundationHolder() {
    }

    public static void addFoundation(int simple, int found) {
        foundations.put(simple, found);
    }

    public static int getFoundation(int id) {
        if (foundations.containsKey(id)) {
            return foundations.get(id);
        }
        return -1;
    }

    public static int size() {
        return foundations.size();
    }
}