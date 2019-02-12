package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.clientpackets.RequestRefineCancel;

/**
 * @author VISTALL
 */
public class ExPutItemResultForVariationCancel extends L2GameServerPacket {
    private final int _itemObjectId;
    private final int _itemId;
    private final int _aug1;
    private final int _aug2;
    private final long _price;

    public ExPutItemResultForVariationCancel(ItemInstance item) {
        _itemObjectId = item.objectId();
        _itemId = item.getItemId();
        _aug1 = 0x0000FFFF & item.getAugmentationId();
        _aug2 = item.getAugmentationId() >> 16;
        _price = RequestRefineCancel.getRemovalPrice(item.getTemplate());
    }

    @Override
    protected void writeImpl() {
        writeEx(0x57);
        writeD(_itemObjectId);
        writeD(_itemId);
        writeD(_aug1);
        writeD(_aug2);
        writeQ(_price);
        writeD(0x01);
    }
}