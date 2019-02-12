package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.TradeItem;

import java.util.ArrayList;
import java.util.List;


public final class PrivateStoreListBuy extends L2GameServerPacket {
    private final int buyerId;
    private final long adena;
    private final List<TradeItem> sellList;

    public PrivateStoreListBuy(Player seller, Player buyer) {
        adena = seller.getAdena();
        buyerId = buyer.objectId();
        sellList = new ArrayList<>();

        for (TradeItem bi : buyer.getBuyList()) {
            TradeItem si = null;
            for (ItemInstance item : seller.getInventory().getItems())
                if (item.getItemId() == bi.getItemId() && item.canBeTraded(seller)) {
                    si = new TradeItem(item);
                    sellList.add(si);
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
                sellList.add(si);
            }
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xBE);

        writeD(buyerId);
        writeQ(adena);
        writeD(sellList.size());
        sellList.forEach(si -> {
            writeItemInfo(si, si.getCurrentValue());
            writeD(si.getObjectId());
            writeQ(si.getOwnersPrice());
            writeQ(si.getStorePrice());
            writeQ(si.getCount()); // maximum possible tradecount
        });
    }
}