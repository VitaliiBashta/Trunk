package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.reward.CalculateRewardChances;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.*;


public final class ItemHolder extends AbstractHolder {
    private static final ItemHolder _instance = new ItemHolder();

    private final Map<Integer,ItemTemplate> _items = new HashMap<>();
//    private List<ItemTemplate> _allTemplates;
    private List<ItemTemplate> droppableTemplates;

    private ItemHolder() {
        //
    }

    public static ItemHolder getInstance() {
        return _instance;
    }

    public void addItem(ItemTemplate template) {
        _items.put(template.getItemId(), template);
    }

//    private void buildFastLookupTable() {
//        int highestId = 0;
//
//        for (int id : _items.keys())
//            if (id > highestId)
//                highestId = id;
//
//        _allTemplates = new ItemTemplate[highestId + 1];
//
//        for (TIntObjectIterator<ItemTemplate> iterator = _items.iterator(); iterator.hasNext(); ) {
//            iterator.advance();
//            _allTemplates[iterator.key()] = iterator.value();
//        }
//    }

    /**
     * Returns the item corresponding to the item ID
     *
     */
    public ItemTemplate getTemplate(int id) {
        if (!_items.containsKey(id)) {
            warn("Not defined item id : " + id + ", or out of range!", new Exception());
        }
        return _items.get(id);
    }

    public Collection<ItemTemplate> getAllTemplates() {
        return _items.values();
    }

    public List<ItemTemplate> getItemsByNameContainingString(String name, boolean onlyDroppable) {
        Collection<ItemTemplate> toChooseFrom = onlyDroppable ? getDroppableTemplates() : _items.values();
        List<ItemTemplate> templates = new ArrayList<>();
        for (ItemTemplate template : toChooseFrom)
            if (template != null && StringUtils.containsIgnoreCase(template.getName(), name))
                templates.add(template);
        return templates;
    }

    public List<ItemTemplate> getDroppableTemplates() {
        if (droppableTemplates == null) {
            droppableTemplates = CalculateRewardChances.getDroppableItems();
        }
        return droppableTemplates;
    }

    @Override
    protected void process() {
//        buildFastLookupTable();
    }

    @Override
    public int size() {
        return _items.size();
    }

    @Override
    public void clear() {
        _items.clear();
    }
}