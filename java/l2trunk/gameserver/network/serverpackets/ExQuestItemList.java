package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.LockType;

import java.util.List;

/**
 * @author VISTALL
 * @date 1:02/23.02.2011
 */
public final class ExQuestItemList extends L2GameServerPacket {
    private final int _size;
    private final List<ItemInstance> _items;

    private final LockType _lockType;
    private final int[] _lockItems;

    public ExQuestItemList(int size, List<ItemInstance> t, LockType lockType, int[] lockItems) {
        _size = size;
        _items = t;
        _lockType = lockType;
        _lockItems = lockItems;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xC6);
        writeH(_size);

        for (ItemInstance temp : _items) {
            if (!temp.getTemplate().isQuest())
                continue;

            writeItemInfo(temp);
        }

        writeH(_lockItems.length);
        if (_lockItems.length > 0) {
            writeC(_lockType.ordinal());
            for (int i : _lockItems)
                writeD(i);
        }
    }
}
