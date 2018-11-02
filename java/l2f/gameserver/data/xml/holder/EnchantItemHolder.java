package l2f.gameserver.data.xml.holder;

import l2f.commons.data.xml.AbstractHolder;
import l2f.gameserver.templates.item.support.EnchantScroll;

import java.util.HashMap;
import java.util.Map;

public class EnchantItemHolder extends AbstractHolder {
    private static EnchantItemHolder _instance = new EnchantItemHolder();

    private Map<Integer,EnchantScroll> _enchantScrolls = new HashMap<>();

    private EnchantItemHolder() {
    }

    public static EnchantItemHolder getInstance() {
        return _instance;
    }

    public void addEnchantScroll(EnchantScroll enchantScroll) {
        _enchantScrolls.put(enchantScroll.getItemId(), enchantScroll);
    }

    public EnchantScroll getEnchantScroll(int id) {
        return _enchantScrolls.get(id);
    }

    public int[] getEnchantScrolls() {
        return _enchantScrolls.keySet().stream().mapToInt(Number::intValue).toArray();
    }

    @Override
    public void log() {
        info("load " + _enchantScrolls.size() + " enchant scroll(s).");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }
}
