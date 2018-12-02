package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.List;

public final class ExGMViewQuestItemList extends L2GameServerPacket {
    private final int size;
    private final List<ItemInstance> items;

    private final int _limit;
    private final String _name;

    public ExGMViewQuestItemList(Player player, List<ItemInstance> items, int size) {
        this.items = items;
        this.size = size;
        _name = player.getName();
        _limit = Config.QUEST_INVENTORY_MAXIMUM;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0xC7);
        writeS(_name);
        writeD(_limit);
        writeH(size);
        for (ItemInstance temp : items)
            if (temp.getTemplate().isQuest())
                writeItemInfo(temp);
    }
}
