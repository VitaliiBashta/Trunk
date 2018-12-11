package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.item.support.EnchantScroll;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class EnchantItemHolder {

    private static final Map<Integer, EnchantScroll> ENCHANT_SCROLLS = new HashMap<>();

    private EnchantItemHolder() {
    }

    public static void addEnchantScroll(EnchantScroll enchantScroll) {
        ENCHANT_SCROLLS.put(enchantScroll.getItemId(), enchantScroll);
    }

    public static int size() {
        return ENCHANT_SCROLLS.size();
    }

    public static EnchantScroll getEnchantScroll(int id) {
        return ENCHANT_SCROLLS.get(id);
    }

    public static Set<Integer> getEnchantScrolls() {
        return ENCHANT_SCROLLS.keySet();
    }

    public void clear() {

    }
}
