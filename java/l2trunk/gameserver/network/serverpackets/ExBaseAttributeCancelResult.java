package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.items.ItemInstance;

/**
 * @author VISTALL
 */
public class ExBaseAttributeCancelResult extends L2GameServerPacket {
    private final boolean _result;
    private final int _objectId;
    private final Element _element;

    public ExBaseAttributeCancelResult(boolean result, ItemInstance item, Element element) {
        _result = result;
        _objectId = item.getObjectId();
        _element = element;
    }

    @Override
    protected void writeImpl() {
        writeEx(0x75);
        writeD(_result);
        writeD(_objectId);
        writeD(_element.getId());
    }
}