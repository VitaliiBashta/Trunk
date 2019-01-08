package l2trunk.scripts.handler.items;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.reward.RewardData;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.FishTable;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.List;

public final class FishItem extends ScriptItemHandler implements ScriptFile {
    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = (Player) playable;

        if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - 10) {
            player.sendPacket(Msg.YOUR_INVENTORY_IS_FULL);
            return false;
        }

        if (!player.getInventory().destroyItem(item, 1L, "FishItem")) {
            player.sendActionFailed();
            return false;
        }

        int count = 0;
        List<RewardData> rewards = FishTable.INSTANCE.getFishReward(item.getItemId());
        for (RewardData d : rewards) {
            long roll = Util.rollDrop(d.getMinDrop(), d.getMaxDrop(), d.getChance() * Config.RATE_FISH_DROP_COUNT * Config.RATE_DROP_ITEMS * player.getRateItems(), false);
            if (roll > 0) {
                ItemFunctions.addItem(player, d.getItemId(), roll, true, "FishItem");
                count++;
            }
        }
        if (count == 0)
            player.sendPacket(SystemMsg.THERE_WAS_NOTHING_FOUND_INSIDE);
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return new ArrayList<>(FishTable.INSTANCE.getFishIds());
    }
}