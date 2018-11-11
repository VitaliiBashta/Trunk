package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.templates.item.support.EnchantScroll;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnchantItemHolder extends AbstractHolder {
    private static final EnchantItemHolder _instance = new EnchantItemHolder();

    private final Map<Integer,EnchantScroll> _enchantScrolls = new HashMap<>();

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

    public Set<Integer> getEnchantScrolls() {
        return _enchantScrolls.keySet();
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
