package l2trunk.gameserver.handler.items;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public enum ItemHandler {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(ItemHandler.class);

    public void registerItemHandler(IItemHandler handler) {
        List<Integer> ids = handler.getItemIds();
        for (int itemId : ids) {
            ItemTemplate template = ItemHolder.INSTANCE.getTemplate(itemId);
            if (template == null)
                LOG.warn("Item not found: " + itemId + " handler: " + handler.getClass().getSimpleName());
            else if (template.getHandler() != IItemHandler.NULL)
                LOG.warn("Duplicate handler for item: " + itemId + "(" + template.getHandler().getClass().getSimpleName() + "," + handler.getClass().getSimpleName() + ")");
            else
                template.setHandler(handler);
        }
    }
}
