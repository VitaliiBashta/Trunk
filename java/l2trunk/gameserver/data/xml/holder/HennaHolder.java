package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HennaHolder {
    private static final Map<Integer, Henna> hennas = new HashMap<>();

    private HennaHolder() {
    }

    public static void addHenna(Henna h) {
        hennas.put(h.symbolId, h);
    }

    public static Henna getHenna(int symbolId) {
        return hennas.get(symbolId);
    }

    public static Stream<Henna> generateStream(Player player) {
        return hennas.values().stream()
                .filter(henna -> henna.isForThisClass(player));
    }

    public static boolean isHenna(int itemId) {
        return hennas.values().stream().anyMatch(h -> h.dyeId == itemId);
    }

    public static int size() {
        return hennas.size();
    }

}
