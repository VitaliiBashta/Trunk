package l2trunk.gameserver.templates.item.support;

import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EnchantItem {
    private final int _itemId;
    private final int _chance;
    private final int _maxEnchant;

    private Set<Integer> items = new HashSet<>();//Containers.EMPTY_INT_SET;
    private final Set<ItemTemplate.Grade> _grades = Collections.emptySet();

    EnchantItem(int itemId, int chance, int maxEnchant) {
        _itemId = itemId;
        _chance = chance;
        _maxEnchant = maxEnchant;
    }

    public void addItemId(int id) {
        if (items.isEmpty())
            items = new HashSet<>();

        items.add(id);
    }

    public int getItemId() {
        return _itemId;
    }

    public int getChance() {
        return _chance;
    }

    public int getMaxEnchant() {
        return _maxEnchant;
    }

    public Set<ItemTemplate.Grade> getGrades() {
        return _grades;
    }

    public Set<Integer> getItems() {
        return items;
    }
}
