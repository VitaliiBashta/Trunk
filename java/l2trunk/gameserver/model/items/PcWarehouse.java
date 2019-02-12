package l2trunk.gameserver.model.items;

import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;

public final class PcWarehouse extends Warehouse {
    public PcWarehouse(int owner) {
        super(owner);
    }

    @Override
    public ItemLocation getItemLocation() {
        return ItemLocation.WAREHOUSE;
    }
}