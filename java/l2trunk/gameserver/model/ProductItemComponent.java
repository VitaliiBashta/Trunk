package l2trunk.gameserver.model;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.templates.item.ItemTemplate;

public final class ProductItemComponent {
    public final int itemId;
    public final int count;
    public final int weight;
    public final boolean dropable;

    public ProductItemComponent(int itemId, int count) {
        this.itemId = itemId;
        this.count = count;
        ItemTemplate item = ItemHolder.getTemplate(itemId);
        weight = item.weight;
        dropable = item.isDropable();

    }

}
