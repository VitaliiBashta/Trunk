package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.DressArmorData;

import java.util.ArrayList;
import java.util.List;

public final class DressArmorHolder {
    private static final List<DressArmorData> DRESS = new ArrayList<>();

    private DressArmorHolder() {
    }

    public static void addDress(DressArmorData armorset) {
        DRESS.add(armorset);
    }

    public static List<DressArmorData> getAllDress() {
        return DRESS;
    }

    public static DressArmorData getArmor(int id) {
        return DRESS.stream()
                .filter(dress -> dress.getId() == id)
                .findFirst().orElse(null);
    }

    public static DressArmorData getArmorByPartId(int partId) {
        return DRESS.stream()
                .filter(dress -> (dress.getChest() == partId || dress.getLegs() == partId || dress.getGloves() == partId || dress.getFeet() == partId))
                .findFirst().orElse(null);
    }

    public static int size() {
        return DRESS.size();
    }

    public void clear() {
        DRESS.clear();
    }
}
