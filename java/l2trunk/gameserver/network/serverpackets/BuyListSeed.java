package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2trunk.gameserver.model.items.TradeItem;

import java.util.ArrayList;
import java.util.List;


/**
 * Format: c ddh[hdddhhd]
 * c - id (0xE8)
 * <p>
 * d - money
 * d - manor id
 * h - size
 * [
 * h - item type 1
 * d - object id
 * d - item id
 * d - count
 * h - item type 2
 * h
 * d - price
 * ]
 */
public final class BuyListSeed extends L2GameServerPacket {
    private final int _manorId;
    private List<TradeItem> _list = new ArrayList<>();
    private final long _money;

    public BuyListSeed(NpcTradeList list, int manorId, long currentMoney) {
        _money = currentMoney;
        _manorId = manorId;
        _list = list.getItems();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xe9);

        writeQ(_money); // current money
        writeD(_manorId); // manor id

        writeH(_list.size()); // list length

        for (TradeItem item : _list) {
            writeItemInfo(item);
            writeQ(item.getOwnersPrice());
        }
    }
}