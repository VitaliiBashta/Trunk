package l2trunk.scripts.handler.items;

import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class Kamaloka extends SimpleItemHandler implements ScriptFile {
    @Override
    public List<Integer> getItemIds() {
        return List.of(13010, 13297, 20026, 13011, 13298, 20027, 13012, 13299, 20028);
    }


    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();
        int count = 0;
        switch (itemId) {
            case 13010:
            case 13297:
            case 20026:
                for (int i : InstantZoneHolder.getSharedReuseInstanceIdsByGroup(1))
                    if (player.getInstanceReuse(i) != null)
                        count++;
                if (count == 0)
                    return false;
                useItem(player, item, 1);
                player.removeInstanceReusesByGroupId(1);
                break;
            case 13011:
            case 13298:
            case 20027:
                for (int i : InstantZoneHolder.getSharedReuseInstanceIdsByGroup(2))
                    if (player.getInstanceReuse(i) != null)
                        count++;
                if (count == 0)
                    return false;
                useItem(player, item, 1);
                player.removeInstanceReusesByGroupId(2);
                break;
            case 13012:
            case 13299:
            case 20028:
                for (int i : InstantZoneHolder.getSharedReuseInstanceIdsByGroup(3))
                    if (player.getInstanceReuse(i) != null)
                        count++;
                if (count == 0)
                    return false;
                useItem(player, item, 1);
                player.removeInstanceReusesByGroupId(3);
                break;
        }
        player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
        return false;
    }
}
