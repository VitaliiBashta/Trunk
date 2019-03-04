package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Recipe;

public final class RecipeItemMakeInfo extends L2GameServerPacket {
    private final int id;
    private final boolean isDwarvenRecipe;
    private final int status;
    private final int curMP;
    private final int maxMP;

    public RecipeItemMakeInfo(Player player, Recipe recipeList, int status) {
        id = recipeList.id;
        isDwarvenRecipe = recipeList.isDwarvenCraft;
        this.status = status;
        curMP = (int) player.getCurrentMp();
        maxMP = player.getMaxMp();
        //
    }

    @Override
    protected final void writeImpl() {
        writeC(0xdd);
        writeD(id); //ID рецепта
        writeD(isDwarvenRecipe ? 0x00 : 0x01);
        writeD(curMP);
        writeD(maxMP);
        writeD(status); //итог крафта; 0xFFFFFFFF нет статуса, 0 удача, 1 провал
    }
}