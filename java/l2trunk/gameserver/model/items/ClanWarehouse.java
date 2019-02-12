package l2trunk.gameserver.model.items;

import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import l2trunk.gameserver.model.pledge.Clan;

public final class ClanWarehouse extends Warehouse {
    private final Clan owner;

    public ClanWarehouse(Clan clan) {
        super(clan.clanId());
        owner = clan;
    }

    public void destroyItem(ItemInstance item, String log) {
         destroyItem(item, owner.toString(), log);
    }

    public boolean destroyItemByItemId(int itemId, long count, String log) {
        return destroyItemByItemId(itemId, count, owner.toString(), log);
    }

    public boolean destroyItemByObjectId(int objectId, long count, String log) {
        return destroyItemByObjectId(objectId, count, owner.toString(), log);
    }

    public ItemInstance addItem(int itemId, long count, String log) {
        return addItem(itemId, count, owner.toString(), log);
    }

    @Override
    public ItemLocation getItemLocation() {
        return ItemLocation.CLANWH;
    }
}