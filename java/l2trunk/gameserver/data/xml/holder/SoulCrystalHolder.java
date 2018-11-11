package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.templates.SoulCrystal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class SoulCrystalHolder extends AbstractHolder {
    private static final SoulCrystalHolder INSTANCE = new SoulCrystalHolder();
    private final Map<Integer, SoulCrystal> crystals = new HashMap<>();

    private SoulCrystalHolder() {
    }

    public static SoulCrystalHolder getInstance() {
        return INSTANCE;
    }

    public void addCrystal(SoulCrystal crystal) {
        crystals.put(crystal.getItemId(), crystal);
    }

    public SoulCrystal getCrystal(int item) {
        return crystals.get(item);
    }

    public Collection<SoulCrystal> getCrystals() {
        return crystals.values();
    }

    @Override
    public int size() {
        return crystals.size();
    }

    @Override
    public void clear() {
        crystals.clear();
    }
}
