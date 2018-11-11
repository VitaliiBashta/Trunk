package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.ArrayList;
import java.util.List;

public class TradeStart extends L2GameServerPacket {
    private final List<ItemInfo> _tradelist = new ArrayList<>();
    private final int targetId;

    public TradeStart(Player player, Player target) {
        targetId = target.getObjectId();

        ItemInstance[] items = player.getInventory().getItems();
        for (ItemInstance item : items)
            if (item.canBeTraded(player))
                _tradelist.add(new ItemInfo(item));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x14);
        writeD(targetId);
        writeH(_tradelist.size());
        for (ItemInfo item : _tradelist)
            writeItemInfo(item);
    }
}