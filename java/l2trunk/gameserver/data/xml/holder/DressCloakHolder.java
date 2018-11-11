package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.DressCloakData;

import java.util.ArrayList;
import java.util.List;

public final class DressCloakHolder extends AbstractHolder {
    private static final DressCloakHolder _instance = new DressCloakHolder();
    private final List<DressCloakData> _cloak = new ArrayList<>();

    public static DressCloakHolder getInstance() {
        return _instance;
    }

    public void addCloak(DressCloakData cloak) {
        _cloak.add(cloak);
    }

    public List<DressCloakData> getAllCloaks() {
        return _cloak;
    }

    public DressCloakData getCloak(int id) {
        for (DressCloakData cloak : _cloak) {
            if (cloak.getId() == id)
                return cloak;
        }

        return null;
    }

    @Override
    public int size() {
        return _cloak.size();
    }

    @Override
    public void clear() {
        _cloak.clear();
    }
}
