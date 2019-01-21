package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Recipe;

/**
 * format ddddd
 */
public final class RecipeItemMakeInfo extends L2GameServerPacket {
    private final int _id;
    private final boolean _isDwarvenRecipe;
    private final int _status;
    private final int _curMP;
    private final int _maxMP;

    public RecipeItemMakeInfo(Player player, Recipe recipeList, int status) {
        _id = recipeList.getId();
        _isDwarvenRecipe = recipeList.isDwarvenRecipe();
        _status = status;
        _curMP = (int) player.getCurrentMp();
        _maxMP = player.getMaxMp();
        //
    }

    @Override
    protected final void writeImpl() {
        writeC(0xdd);
        writeD(_id); //ID рецепта
        writeD(_isDwarvenRecipe ? 0x00 : 0x01);
        writeD(_curMP);
        writeD(_maxMP);
        writeD(_status); //итог крафта; 0xFFFFFFFF нет статуса, 0 удача, 1 провал
    }
}