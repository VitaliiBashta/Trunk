package l2trunk.gameserver.handler.items;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Collection;
import java.util.List;

public interface IItemHandler {
    IItemHandler NULL = new IItemHandler() {
        @Override
        public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
            return false;
        }

        @Override
        public void dropItem(Player player, ItemInstance item, long count, Location loc) {
            if (item.isEquipped()) {
                player.inventory.unEquipItem(item);
                player.sendUserInfo(true);
            }
            item = player.inventory.removeItemByObjectId(item.objectId(), count, "DropItem");
            if (item == null) {
                player.sendActionFailed();
                return;
            }
            item.dropToTheGround(player, loc);
            player.disableDrop(1000);
            player.sendChanges();
        }

        @Override
        public List<Integer> getItemIds() {
            return List.of();
        }
    };

    boolean useItem(Player player, ItemInstance item, boolean ctrl);

    void dropItem(Player player, ItemInstance item, long count, Location loc);

    default boolean pickupItem(Player player, ItemInstance item){
        return true;
    }

    Collection<Integer> getItemIds();
}
