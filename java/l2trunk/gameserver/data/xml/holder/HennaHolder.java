package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HennaHolder extends AbstractHolder {
    private static final HennaHolder INSTANCE = new HennaHolder();

    private final Map<Integer,Henna> hennas = new HashMap<>();

    public static HennaHolder getInstance() {
        return INSTANCE;
    }

    public void addHenna(Henna h) {
        hennas.put(h.getSymbolId(), h);
    }

    public Henna getHenna(int symbolId) {
        return hennas.get(symbolId);
    }

    public List<Henna> generateList(Player player) {
        List<Henna> list = new ArrayList<>();
        for (Henna henna : hennas.values()) {
            if (henna.isForThisClass(player))
                list.add(henna);
        }

        return list;
    }

    public boolean isHenna(int itemId) {
        return hennas.entrySet().stream().anyMatch( h -> h.getValue().getDyeId() == itemId);
    }

    @Override
    public int size() {
        return hennas.size();
    }

    @Override
    public void clear() {
        hennas.clear();
    }
}
