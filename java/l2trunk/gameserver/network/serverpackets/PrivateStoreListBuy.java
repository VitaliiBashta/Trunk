package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.TradeItem;

import java.util.ArrayList;
import java.util.List;


public final class PrivateStoreListBuy extends L2GameServerPacket {
    private final int _buyerId;
    private final long _adena;
    private final List<TradeItem> _sellList;

    public PrivateStoreListBuy(Player seller, Player buyer) {
        _adena = seller.getAdena();
        _buyerId = buyer.getObjectId();
        _sellList = new ArrayList<>();

        List<TradeItem> buyList = buyer.getBuyList();
        List<ItemInstance> items = seller.getInventory().getItems();

        for (TradeItem bi : buyList) {
            TradeItem si = null;
            for (ItemInstance item : items)
                if (item.getItemId() == bi.getItemId() && item.canBeTraded(seller)) {
                    si = new TradeItem(item);
                    _sellList.add(si);
                    si.setOwnersPrice(bi.getOwnersPrice());
                    si.setCount(bi.getCount());
                    si.setCurrentValue(Math.min(bi.getCount(), item.getCount()));
                }
            if (si == null) {
                si = new TradeItem();
                si.setItemId(bi.getItemId());
                si.setOwnersPrice(bi.getOwnersPrice());
                si.setCount(bi.getCount());
                si.setCurrentValue(0);
                _sellList.add(si);
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xBE);

        writeD(_buyerId);
        writeQ(_adena);
        writeD(_sellList.size());
        for (TradeItem si : _sellList) {
            writeItemInfo(si, si.getCurrentValue());
            writeD(si.getObjectId());
            writeQ(si.getOwnersPrice());
            writeQ(si.getStorePrice());
            writeQ(si.getCount()); // maximum possible tradecount
        }
    }
}