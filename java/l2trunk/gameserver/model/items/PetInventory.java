package l2trunk.gameserver.model.items;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import l2trunk.gameserver.network.serverpackets.PetInventoryUpdate;
import l2trunk.gameserver.utils.ItemFunctions;

public class PetInventory extends Inventory {
    private final PetInstance _actor;

    public PetInventory(PetInstance actor) {
        super(actor.getPlayer().getObjectId());
        _actor = actor;
    }

    @Override
    public PetInstance getActor() {
        return _actor;
    }

    private Player getOwner() {
        return _actor.getPlayer();
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.PET_INVENTORY;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.PET_PAPERDOLL;
    }

    @Override
    protected void onRefreshWeight() {
        getActor().sendPetInfo();
    }

    @Override
    protected void sendAddItem(ItemInstance item) {
        getOwner().sendPacket(new PetInventoryUpdate().addNewItem(item));
    }

    @Override
    protected void sendModifyItem(ItemInstance item) {
        getOwner().sendPacket(new PetInventoryUpdate().addModifiedItem(item));
    }

    @Override
    protected void sendRemoveItem(ItemInstance item) {
        getOwner().sendPacket(new PetInventoryUpdate().addRemovedItem(item));
    }

    @Override
    public void restore() {
        final int ownerId = getOwnerId();

        writeLock();
        try {

            ITEMS_DAO.getItemsByOwnerIdAndLoc(ownerId, getBaseLocation()).forEach(item -> {
                this.items.add(item);
                onRestoreItem(item);
            });

            ITEMS_DAO.getItemsByOwnerIdAndLoc(ownerId, getEquipLocation()).forEach(item -> {
                this.items.add(item);
                onRestoreItem(item);
                if (ItemFunctions.checkIfCanEquip(getActor(), item) == null)
                    setPaperdollItem(item.getEquipSlot(), item);
            });
        } finally {
            writeUnlock();
        }

        refreshWeight();
    }

    @Override
    public void store() {
        writeLock();
        try {
            ITEMS_DAO.update(items);
        } finally {
            writeUnlock();
        }
    }

    public void validateItems() {
        for (ItemInstance item : paperdoll)
            if (item != null && (ItemFunctions.checkIfCanEquip(getActor(), item) != null || !item.getTemplate().testCondition(getActor(), item)))
                unEquipItem(item);
    }

    public boolean destroyItem(ItemInstance item, long count, String log) {
        return destroyItem(item, count, "Pet " + _actor.getPlayer().toString(), log);
    }

    public boolean destroyItem(ItemInstance item, String log) {
        return destroyItem(item, "Pet " + _actor.getPlayer().toString(), log);
    }

    public boolean destroyItemByItemId(int itemId, long count, String log) {
        return destroyItemByItemId(itemId, count, "Pet " + _actor.getPlayer().toString(), log);
    }

    public boolean destroyItemByObjectId(int objectId, long count, String log) {
        return destroyItemByObjectId(objectId, count, "Pet " + _actor.getPlayer().toString(), log);
    }

    public ItemInstance addItem(ItemInstance item, String log) {
        return addItem(item, "Pet " + _actor.getPlayer().toString(), log);
    }

    public ItemInstance addItem(int itemId, long count, String log) {
        return addItem(itemId, count, "Pet " + _actor.getPlayer().toString(), log);
    }

    public ItemInstance removeItem(ItemInstance item, long count, String log) {
        return removeItem(item, count, "Pet " + _actor.getPlayer().toString(), log);
    }

    public ItemInstance removeItem(ItemInstance item, String log) {
        return removeItem(item, "Pet " + _actor.getPlayer().toString(), log);
    }

    public ItemInstance removeItemByItemId(int itemId, long count, String log) {
        return removeItemByItemId(itemId, count, "Pet " + _actor.getPlayer().toString(), log);
    }

    public ItemInstance removeItemByObjectId(int objectId, long count, String log) {
        return removeItemByObjectId(objectId, count, "Pet " + _actor.getPlayer().toString(), log);
    }
}