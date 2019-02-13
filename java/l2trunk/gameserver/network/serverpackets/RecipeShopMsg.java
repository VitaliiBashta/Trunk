package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public class RecipeShopMsg extends L2GameServerPacket {
    private final int _objectId;
    private final String _storeName;

    public RecipeShopMsg(Player player) {
        _objectId = player.objectId();
        _storeName = player.getManufactureName();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xe1);
        writeD(_objectId);
        writeS(_storeName);
    }
}