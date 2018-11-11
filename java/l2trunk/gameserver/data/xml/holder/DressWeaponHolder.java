package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.DressWeaponData;

import java.util.ArrayList;
import java.util.List;

public final class DressWeaponHolder extends AbstractHolder {
    private static final DressWeaponHolder _instance = new DressWeaponHolder();
    private final List<DressWeaponData> _weapons = new ArrayList<>();

    public static DressWeaponHolder getInstance() {
        return _instance;
    }

    public void addWeapon(DressWeaponData weapon) {
        _weapons.add(weapon);
    }

    public List<DressWeaponData> getAllWeapons() {
        return _weapons;
    }

    public DressWeaponData getWeapon(int id) {
        for (DressWeaponData weapon : _weapons) {
            if (weapon.getId() == id)
                return weapon;
        }

        return null;
    }

    @Override
    public int size() {
        return _weapons.size();
    }

    @Override
    public void clear() {
        _weapons.clear();
    }
}