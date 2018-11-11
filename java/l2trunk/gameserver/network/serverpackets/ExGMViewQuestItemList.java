package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;

/**
 * @author VISTALL
 * @date 4:20/06.05.2011
 */
public class ExGMViewQuestItemList extends L2GameServerPacket {
    private final int _size;
    private final ItemInstance[] _items;

    private final int _limit;
    private final String _name;

    public ExGMViewQuestItemList(Player player, ItemInstance[] items, int size) {
        _items = items;
        _size = size;
        _name = player.getName();
        _limit = Config.QUEST_INVENTORY_MAXIMUM;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0xC7);
        writeS(_name);
        writeD(_limit);
        writeH(_size);
        for (ItemInstance temp : _items)
            if (temp.getTemplate().isQuest())
                writeItemInfo(temp);
    }
}
