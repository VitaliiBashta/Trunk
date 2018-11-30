package l2trunk.gameserver.handler.items;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.List;

public class ItemHandler extends AbstractHolder {
    private static final ItemHandler _instance = new ItemHandler();

    private ItemHandler() {
        //
    }

    public static ItemHandler getInstance() {
        return _instance;
    }

    public void registerItemHandler(IItemHandler handler) {
        List<Integer> ids = handler.getItemIds();
        for (int itemId : ids) {
            ItemTemplate template = ItemHolder.getInstance().getTemplate(itemId);
            if (template == null)
                LOG.warn("Item not found: " + itemId + " handler: " + handler.getClass().getSimpleName());
            else if (template.getHandler() != IItemHandler.NULL)
                LOG.warn("Duplicate handler for item: " + itemId + "(" + template.getHandler().getClass().getSimpleName() + "," + handler.getClass().getSimpleName() + ")");
            else
                template.setHandler(handler);
        }
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }
}
