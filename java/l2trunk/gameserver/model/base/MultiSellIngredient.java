package l2trunk.gameserver.model.base;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.items.ItemAttributes;

public final class MultiSellIngredient implements Cloneable {
    private int itemId;
    private long _itemCount;
    private int itemEnchant;
    private ItemAttributes _itemAttributes;
    private boolean _mantainIngredient;

    public MultiSellIngredient(int itemId, long itemCount) {
        this(itemId, itemCount, 0);
    }

    public MultiSellIngredient(int itemId, long itemCount, int enchant) {
        this.itemId = itemId;
        _itemCount = itemCount;
        itemEnchant = enchant;
        _mantainIngredient = false;
        _itemAttributes = new ItemAttributes();
    }

    @Override
    public MultiSellIngredient clone() {
        MultiSellIngredient mi = new MultiSellIngredient(itemId, _itemCount, itemEnchant);
        mi.setMantainIngredient(_mantainIngredient);
        mi.setItemAttributes(_itemAttributes.clone());
        return mi;
    }

    /**
     * @return Returns the itemId.
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * @param itemId The itemId to set.
     */
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    /**
     * @return Returns the itemCount.
     */
    public long getItemCount() {
        return _itemCount;
    }

    /**
     * @param itemCount The itemCount to set.
     */
    public void setItemCount(long itemCount) {
        _itemCount = itemCount;
    }

    public boolean isStackable() {
        return itemId <= 0 || ItemHolder.getTemplate(itemId).isStackable();
    }

    public int getItemEnchant() {
        return itemEnchant;
    }

    public void setItemEnchant(int itemEnchant) {
        this.itemEnchant = itemEnchant;
    }

    public ItemAttributes getItemAttributes() {
        return _itemAttributes;
    }

    public void setItemAttributes(ItemAttributes attr) {
        _itemAttributes = attr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (_itemCount ^ _itemCount >>> 32);
        for (Element e : Element.VALUES)
            result = prime * result + _itemAttributes.getValue(e);
        result = prime * result + itemEnchant;
        result = prime * result + itemId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MultiSellIngredient other = (MultiSellIngredient) obj;
        if (itemId != other.itemId)
            return false;
        if (_itemCount != other._itemCount)
            return false;
        if (itemEnchant != other.itemEnchant)
            return false;
        for (Element e : Element.VALUES)
            if (_itemAttributes.getValue(e) != other._itemAttributes.getValue(e))
                return false;
        return true;
    }

    public boolean getMantainIngredient() {
        return _mantainIngredient;
    }

    public void setMantainIngredient(boolean mantainIngredient) {
        _mantainIngredient = mantainIngredient;
    }
}