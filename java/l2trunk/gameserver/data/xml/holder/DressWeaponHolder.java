package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.DressWeaponData;

import java.util.ArrayList;
import java.util.List;

public final class DressWeaponHolder {
    private static final List<DressWeaponData> WEAPONS = new ArrayList<>();

    private DressWeaponHolder() {
    }

    public static void addWeapon(DressWeaponData weapon) {
        WEAPONS.add(weapon);
    }

    public static int size() {
        return WEAPONS.size();
    }

    public void clear() {
        WEAPONS.clear();
    }
}
