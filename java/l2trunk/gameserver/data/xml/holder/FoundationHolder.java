package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;

import java.util.HashMap;
import java.util.Map;

public final class FoundationHolder extends AbstractHolder {
    private static final FoundationHolder _instance = new FoundationHolder();

    private final Map<Integer, Integer> _foundation = new HashMap<>();

    public static FoundationHolder getInstance() {
        return _instance;
    }

    public void addFoundation(int simple, int found) {
        this._foundation.put(simple, found);
    }

    public int getFoundation(int id) {
        if (this._foundation.containsKey(id)) {
            return _foundation.get(id);
        }
        return -1;
    }

    @Override
    public int size() {
        return this._foundation.size();
    }

    @Override
    public void clear() {
        this._foundation.clear();
    }
}