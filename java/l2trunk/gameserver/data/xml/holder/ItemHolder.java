package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.reward.CalculateRewardChances;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ItemHolder extends AbstractHolder {
//    instance;
    private final Map<Integer, ItemTemplate> items = new HashMap<>();
    private List<ItemTemplate> droppableTemplates;
    private static final Logger LOG = LoggerFactory.getLogger(ItemHolder.class);
    private static ItemHolder instance = new ItemHolder();

    public static ItemHolder getInstance() {
        return instance;
    }

    public void addItem(ItemTemplate template) {
        items.put(template.getItemId(), template);
    }

    public ItemTemplate getTemplate(int id) {
        if (!items.containsKey(id)) {
            LOG.warn("Not defined item id : " + id + ", or out of range!", new Exception());
        }
        return items.get(id);
    }

    public Collection<ItemTemplate> getAllTemplates() {
        return items.values();
    }

    public List<ItemTemplate> getItemsByNameContainingString(String name, boolean onlyDroppable) {
        Collection<ItemTemplate> toChooseFrom = onlyDroppable ? getDroppableTemplates() : items.values();
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
    protected int size() {
        return items.size();
    }

    @Override
    public void clear() {
        items.clear();
    }
}