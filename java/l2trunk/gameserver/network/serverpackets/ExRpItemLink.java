package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.items.ItemInfo;

/**
 * ddQhdhhhhhdhhhhhhhh - Gracia Final
 */
public class ExRpItemLink extends L2GameServerPacket {
    private final ItemInfo _item;

    public ExRpItemLink(ItemInfo item) {
        _item = item;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x6c);
        writeItemInfo(_item);
    }
}