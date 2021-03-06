package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.TradeItem;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;


public final class PrivateStoreManageListSell extends L2GameServerPacket {
    private final int _sellerId;
    private final long _adena;
    private final boolean _package;
    private final List<TradeItem> _sellList;
    private final List<TradeItem> _sellList0;

    /**
     * Окно управления личным магазином продажи
     */
    public PrivateStoreManageListSell(Player seller, boolean pkg) {
        _sellerId = seller.objectId();
        _adena = seller.getAdena();
        _package = pkg;
        _sellList0 = seller.getSellList(_package);
        _sellList = new ArrayList<>();

        // Проверяем список вещей в инвентаре, если вещь остутствует - убираем из списка продажи
        for (TradeItem si : _sellList0) {
            if (si.getCount() <= 0) {
                _sellList0.remove(si);
                continue;
            }

            ItemInstance item = seller.getInventory().getItemByObjectId(si.getObjectId());
            if (item == null)
                //вещь недоступна, пробуем найти такую же по itemId
                item = seller.getInventory().getItemByItemId(si.getItemId());

            if (item == null || !item.canBeTraded(seller) || item.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                _sellList0.remove(si);
                continue;
            }

            //корректируем количество
            si.setCount(Math.min(item.getCount(), si.getCount()));
        }

        List<ItemInstance> items = seller.getInventory().getItems();
        // Проверяем список вещей в инвентаре, если вещь остутствует в списке продажи, добавляем в список доступных для продажи
        loop:
        for (ItemInstance item : items)
            if (item.canBeTraded(seller) && item.getItemId() != ItemTemplate.ITEM_ID_ADENA) {
                for (TradeItem si : _sellList0)
                    if (si.getObjectId() == item.objectId()) {
                        if (si.getCount() == item.getCount())
                            continue loop;
                        // Показывает остаток вещей для продажи
                        TradeItem ti = new TradeItem(item);
                        ti.setCount(item.getCount() - si.getCount());
                        _sellList.add(ti);
                        continue loop;
                    }
                _sellList.add(new TradeItem(item));
            }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xA0);
        //section 1
        writeD(_sellerId);
        writeD(_package ? 1 : 0);
        writeQ(_adena);

        //Список имеющихся вещей
        writeD(_sellList.size());
        for (TradeItem si : _sellList) {
            writeItemInfo(si);
            writeQ(si.getStorePrice());
        }

        //Список вещей уже поставленых на продажу
        writeD(_sellList0.size());
        for (TradeItem si : _sellList0) {
            writeItemInfo(si);
            writeQ(si.getOwnersPrice());
            writeQ(si.getStorePrice());
        }
    }
}