package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class HennaHolder {
    private static final Map<Integer, Henna> hennas = new HashMap<>();

    private HennaHolder() {
    }

    public static void addHenna(Henna h) {
        hennas.put(h.getSymbolId(), h);
    }

    public static Henna getHenna(int symbolId) {
        return hennas.get(symbolId);
    }

    public static List<Henna> generateList(Player player) {
        return hennas.values().stream()
                .filter(henna -> henna.isForThisClass(player))
                .collect(Collectors.toList());
    }

    public static boolean isHenna(int itemId) {
        return hennas.entrySet().stream().anyMatch(h -> h.getValue().getDyeId() == itemId);
    }

    public static int size() {
        return hennas.size();
    }

}
