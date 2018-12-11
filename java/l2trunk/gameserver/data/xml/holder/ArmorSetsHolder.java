package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.ArmorSet;

import java.util.ArrayList;
import java.util.List;


public final class ArmorSetsHolder {
    private static final List<ArmorSet> ARMOR_SETS = new ArrayList<>();

    private ArmorSetsHolder() {
    }

    public static int size() {
        return ARMOR_SETS.size();
    }

    public static void clear() {
        ARMOR_SETS.clear();
    }

    public static void addArmorSet(ArmorSet armorset) {
        ARMOR_SETS.add(armorset);
    }

    public static ArmorSet getArmorSet(int chestItemId) {
        return ARMOR_SETS.stream()
                .filter(as -> as.getChestItemIds().contains(chestItemId))
                .findFirst().orElse(null);
    }
}
