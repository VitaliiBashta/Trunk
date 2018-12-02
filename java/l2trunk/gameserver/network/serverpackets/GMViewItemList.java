package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.List;

public final class GMViewItemList extends L2GameServerPacket {
    private final int _size;
    private final List<ItemInstance> _items;
    private final int _limit;
    private final String _name;

    public GMViewItemList(Player cha, List<ItemInstance> items, int size) {
        _size = size;
        _items = items;
        _name = cha.getName();
        _limit = cha.getInventoryLimit();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x9a);
        writeS(_name);
        writeD(_limit); //c4?
        writeH(1); // show window ??

        writeH(_size);
        for (ItemInstance temp : _items)
            if (!temp.getTemplate().isQuest())
                writeItemInfo(temp);
    }
}