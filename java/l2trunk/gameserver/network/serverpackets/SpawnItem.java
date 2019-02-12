package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.items.ItemInstance;

/**
 * 15
 * ee cc 11 43 		object id
 * 39 00 00 00 		item id
 * 8f 14 00 00 		x
 * b7 f1 00 00 		y
 * 60 f2 ff ff 		z
 * 01 00 00 00 		show item count
 * 7a 00 00 00      count                                         .
 * <p>
 * format  dddddddd
 */
public class SpawnItem extends L2GameServerPacket {
    private final int _objectId;
    private final int _itemId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _stackable;
    private final long _count;

    public SpawnItem(ItemInstance item) {
        _objectId = item.objectId();
        _itemId = item.getItemId();
        _x = item.getX();
        _y = item.getY();
        _z = item.getZ();
        _stackable = item.isStackable() ? 0x01 : 0x00;
        _count = item.getCount();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x05);
        writeD(_objectId);
        writeD(_itemId);

        writeD(_x);
        writeD(_y);
        writeD(_z + Config.CLIENT_Z_SHIFT);
        writeD(_stackable);
        writeQ(_count);
        writeD(0x00); //c2
    }
}