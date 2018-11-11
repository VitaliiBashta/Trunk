package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.exchange.Change;

import java.util.HashMap;
import java.util.Map;

public final class ExchangeItemHolder extends AbstractHolder {
    private static final ExchangeItemHolder _instance = new ExchangeItemHolder();
    private final Map<Integer, Change> _changes = new HashMap<>();
    private final Map<Integer, Change> _upgrades = new HashMap<>();

    public static ExchangeItemHolder getInstance() {
        return _instance;
    }

    public void addChanges(Change armorset) {
        if (armorset.isUpgrade())
            _upgrades.put(armorset.getId(), armorset);
        else
            _changes.put(armorset.getId(), armorset);
    }

    public Change getChanges(int id, boolean isUpgrade) {
        if (isUpgrade)
            return _upgrades.get(id);
        return _changes.get(id);
    }

    @Override
    public int size() {
        return _changes.size() + _upgrades.size();
    }

    @Override
    public void clear() {
        _changes.clear();
        _upgrades.clear();
    }
}
