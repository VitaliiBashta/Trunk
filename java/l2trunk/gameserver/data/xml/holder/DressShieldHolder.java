package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.DressShieldData;

import java.util.ArrayList;
import java.util.List;

public final class DressShieldHolder extends AbstractHolder {
    private static final DressShieldHolder _instance = new DressShieldHolder();
    private final List<DressShieldData> _shield = new ArrayList<>();

    public static DressShieldHolder getInstance() {
        return _instance;
    }

    public void addShield(DressShieldData shield) {
        _shield.add(shield);
    }

    public List<DressShieldData> getAllShields() {
        return _shield;
    }

    public DressShieldData getShield(int id) {
        for (DressShieldData shield : _shield) {
            if (shield.getId() == id)
                return shield;
        }

        return null;
    }

    @Override
    public int size() {
        return _shield.size();
    }

    @Override
    public void clear() {
        _shield.clear();
    }
}
