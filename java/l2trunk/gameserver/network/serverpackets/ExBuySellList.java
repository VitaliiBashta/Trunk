package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.TradeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class ExBuySellList extends L2GameServerPacket {
    private final int _type;

    ExBuySellList(int type) {
        _type = type;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xB7);
        writeD(_type);
    }

    public static class BuyList extends ExBuySellList {
        private final int _listId;
        private final List<TradeItem> _buyList;
        private final long _adena;
        private final double _taxRate;

        public BuyList(NpcTradeList tradeList, Player activeChar, double taxRate) {
            super(0);
            _adena = activeChar.getAdena();
            _taxRate = taxRate;

            if (tradeList != null) {
                _listId = tradeList.getListId();
                _buyList = tradeList.getItems();
                activeChar.setBuyListId(_listId);
            } else {
                _listId = 0;
                _buyList = Collections.emptyList();
                activeChar.setBuyListId(0);
            }
        }

        @Override
        protected void writeImpl() {
            super.writeImpl();
            writeQ(_adena); // current money
            writeD(_listId);
            writeH(_buyList.size());
            for (TradeItem item : _buyList) {
                writeItemInfo(item, item.getCurrentValue());
                writeQ((long) (item.getOwnersPrice() * (1. + _taxRate)));
            }
        }
    }

    public static class SellRefundList extends ExBuySellList {
        private final List<TradeItem> _sellList;
        private final List<TradeItem> _refundList;
        private final int _done;

        public SellRefundList(Player activeChar, boolean done) {
            super(1);
            _done = done ? 1 : 0;
            if (done) {
                _refundList = Collections.emptyList();
                _sellList = Collections.emptyList();
            } else {
                List<ItemInstance> items = activeChar.getRefund().getItems();
                _refundList = new ArrayList<>(items.size());
                items.forEach(item -> _refundList.add(new TradeItem(item)));

                items = activeChar.getInventory().getItems();
                _sellList = new ArrayList<>(items.size());
                for (ItemInstance item : items)
                    if (item.canBeSold(activeChar))
                        _sellList.add(new TradeItem(item));
            }
        }

        @Override
        protected void writeImpl() {
            super.writeImpl();
            writeH(_sellList.size());
            for (TradeItem item : _sellList) {
                writeItemInfo(item);
                writeQ(item.getReferencePrice() / 2);
            }
            writeH(_refundList.size());
            for (TradeItem item : _refundList) {
                writeItemInfo(item);
                writeD(item.getObjectId());
                writeQ(item.getCount() * item.getReferencePrice() / 2);
            }
            writeC(_done);
        }
    }
}