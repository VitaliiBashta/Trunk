package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.RecipeHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Recipe;
import l2trunk.gameserver.network.serverpackets.RecipeItemMakeInfo;

public final class RequestRecipeItemMakeInfo extends L2GameClientPacket {
    private int id;

    /**
     * packet type id 0xB7
     * format:		cd
     */
    @Override
    protected void readImpl() {
        id = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        Recipe recipeList = RecipeHolder.getInstance().getRecipeByRecipeId(id);
        if (recipeList == null) {
            activeChar.sendActionFailed();
            return;
        }

        sendPacket(new RecipeItemMakeInfo(activeChar, recipeList, 0xffffffff));
    }
}