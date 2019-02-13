package l2trunk.gameserver.model.items;

public class PcFreight extends Warehouse {
    public PcFreight(int objectId) {
        super(objectId);
    }

    @Override
    public ItemInstance.ItemLocation getItemLocation() {
        return ItemInstance.ItemLocation.FREIGHT;
    }
}
