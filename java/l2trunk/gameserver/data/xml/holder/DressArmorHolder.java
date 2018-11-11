package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.DressArmorData;

import java.util.ArrayList;
import java.util.List;

public final class DressArmorHolder extends AbstractHolder {
    private static final DressArmorHolder _instance = new DressArmorHolder();
    private final List<DressArmorData> _dress = new ArrayList<>();

    public static DressArmorHolder getInstance() {
        return _instance;
    }

    public void addDress(DressArmorData armorset) {
        _dress.add(armorset);
    }

    public List<DressArmorData> getAllDress() {
        return _dress;
    }

    public DressArmorData getArmor(int id) {
        for (DressArmorData dress : _dress) {
            if (dress.getId() == id)
                return dress;
        }

        return null;
    }

    public DressArmorData getArmorByPartId(int partId) {
        for (DressArmorData dress : _dress) {
            if (dress.getChest() == partId || dress.getLegs() == partId || dress.getGloves() == partId || dress.getFeet() == partId)
                return dress;
        }

        return null;
    }

    @Override
    public int size() {
        return _dress.size();
    }

    @Override
    public void clear() {
        _dress.clear();
    }
}
