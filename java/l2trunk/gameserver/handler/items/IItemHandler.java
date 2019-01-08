package l2trunk.gameserver.handler.items;

import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.Location;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface IItemHandler {
    IItemHandler NULL = new IItemHandler() {
        @Override
        public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
            return false;
        }

        @Override
        public void dropItem(Player player, ItemInstance item, long count, Location loc) {
            if (item.isEquipped()) {
                player.getInventory().unEquipItem(item);
                player.sendUserInfo(true);
            }

            item = player.getInventory().removeItemByObjectId(item.getObjectId(), count, "DropItem");
            if (item == null) {
                player.sendActionFailed();
                return;
            }

            item.dropToTheGround(player, loc);
            player.disableDrop(1000);

            player.sendChanges();
        }

        @Override
        public boolean pickupItem(Playable playable, ItemInstance item) {
            return true;
        }

        @Override
        public List<Integer> getItemIds() {
            return List.of();
        }
    };

    boolean useItem(Playable playable, ItemInstance item, boolean ctrl);

    void dropItem(Player player, ItemInstance item, long count, Location loc);

    boolean pickupItem(Playable playable, ItemInstance item);

    Collection<Integer> getItemIds();
}
