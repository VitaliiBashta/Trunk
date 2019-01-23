package l2trunk.gameserver.model.items;

import l2trunk.gameserver.model.Player;

public final class PcFreight extends Warehouse {
    public PcFreight(Player player) {
        super(player.getObjectId());
    }

    public PcFreight(int objectId) {
        super(objectId);
    }

    @Override
    public ItemInstance.ItemLocation getItemLocation() {
        return ItemInstance.ItemLocation.FREIGHT;
    }
}
