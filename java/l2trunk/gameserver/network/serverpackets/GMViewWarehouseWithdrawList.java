package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.List;

public final class GMViewWarehouseWithdrawList extends L2GameServerPacket {
    private final List<ItemInstance> _items;
    private final String _charName;
    private final long _charAdena;

    public GMViewWarehouseWithdrawList(Player cha) {
        _charName = cha.getName();
        _charAdena = cha.getAdena();
        _items = cha.getWarehouse().getItems();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x9b);
        writeS(_charName);
        writeQ(_charAdena);
        writeH(_items.size());
        _items.forEach(item -> {
            writeItemInfo(item);
            writeD(item.objectId());
        });
    }
}