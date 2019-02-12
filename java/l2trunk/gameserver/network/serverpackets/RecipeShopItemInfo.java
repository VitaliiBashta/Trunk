package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

/**
 * dddddQ
 */
public class RecipeShopItemInfo extends L2GameServerPacket {
    private final int _recipeId;
    private final int _shopId;
    private final int _curMp;
    private final int _maxMp;
    private final long _price;
    private int _success = 0xFFFFFFFF;

    public RecipeShopItemInfo(Player activeChar, Player manufacturer, int recipeId, long price, int success) {
        _recipeId = recipeId;
        _shopId = manufacturer.objectId();
        _price = price;
        _success = success;
        _curMp = (int) manufacturer.getCurrentMp();
        _maxMp = manufacturer.getMaxMp();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xe0);
        writeD(_shopId);
        writeD(_recipeId);
        writeD(_curMp);
        writeD(_maxMp);
        writeD(_success);
        writeQ(_price);
    }
}