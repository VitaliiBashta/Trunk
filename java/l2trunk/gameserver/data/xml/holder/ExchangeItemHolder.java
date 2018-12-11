package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.exchange.Change;

import java.util.HashMap;
import java.util.Map;

public final class ExchangeItemHolder  {
    private ExchangeItemHolder() {
    }

    private static final Map<Integer, Change> CHANGES = new HashMap<>();
    private static final Map<Integer, Change> UPGRADES = new HashMap<>();

    public static void addChanges(Change armorset) {
        if (armorset.isUpgrade())
            UPGRADES.put(armorset.getId(), armorset);
        else
            CHANGES.put(armorset.getId(), armorset);
    }

    public static Change getChanges(int id, boolean isUpgrade) {
        if (isUpgrade)
            return UPGRADES.get(id);
        return CHANGES.get(id);
    }

    public static int size() {
        return CHANGES.size() + UPGRADES.size();
    }

    public static void clear() {
        CHANGES.clear();
        UPGRADES.clear();
    }
}
