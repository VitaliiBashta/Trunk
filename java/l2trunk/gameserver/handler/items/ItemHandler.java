package l2trunk.gameserver.handler.items;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ItemHandler {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(ItemHandler.class);

    public void registerItemHandler(IItemHandler handler) {
        handler.getItemIds().forEach(itemId -> {
            ItemTemplate template = ItemHolder.getTemplate(itemId);
            if (template.getHandler() != IItemHandler.NULL)
                LOG.warn("Duplicate handler for item: " + itemId + "(" + template.getHandler().getClass()
                        + "," + handler.getClass() + ")");
            else
                template.setHandler(handler);
        });
    }
}
