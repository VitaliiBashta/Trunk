package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.LockType;

import java.util.List;

public final class ItemList extends L2GameServerPacket {
    private final int _size;
    private final List<ItemInstance> _items;
    private final boolean _showWindow;

    private final LockType _lockType;
    private final List<Integer> _lockItems;

    public ItemList(int size, List<ItemInstance> items, boolean showWindow, LockType lockType, List<Integer> lockItems) {
        _size = size;
        _items = items;
        _showWindow = showWindow;
        _lockType = lockType;
        _lockItems = lockItems;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x11);
        writeH(_showWindow ? 1 : 0);

        writeH(_size);
        for (ItemInstance temp : _items) {
            if (temp.getTemplate().isQuest())
                continue;

            writeItemInfo(temp);
        }

        writeH(_lockItems.size());
        if (_lockItems.size() > 0) {
            writeC(_lockType.ordinal());
            _lockItems.forEach(this::writeD);
        }
    }
}