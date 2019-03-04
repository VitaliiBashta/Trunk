package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.lang.Pair;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.RecipeHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Recipe;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.RecipeItemMakeInfo;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.EtcItemTemplate.EtcItemType;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.List;

public class RequestRecipeItemMakeSelf extends L2GameClientPacket {
    private int _recipeId;

    /**
     * packet type id 0xB8
     * format:		cd
     */
    @Override
    protected void readImpl() {
        _recipeId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isProcessingRequest()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        Recipe recipeList = RecipeHolder.getInstance().getRecipeByRecipeId(_recipeId);

        if (recipeList == null || recipeList.getRecipes().isEmpty()) {
            activeChar.sendPacket(SystemMsg.THE_RECIPE_IS_INCORRECT);
            return;
        }

        if (activeChar.getCurrentMp() < recipeList.mpCost) {
            activeChar.sendPacket(SystemMsg.NOT_ENOUGH_MP, new RecipeItemMakeInfo(activeChar, recipeList, 0));
            return;
        }

        if (!activeChar.findRecipe(_recipeId)) {
            activeChar.sendPacket(SystemMsg.PLEASE_REGISTER_A_RECIPE, ActionFail.STATIC);
            return;
        }

        activeChar.getInventory().writeLock();
        try {
            List<Pair<Integer, Integer>> recipes = recipeList.getRecipes();

            for (Pair<Integer, Integer> recipe : recipes) {
                if (recipe.getValue() == 0)
                    continue;

                if (Config.ALT_GAME_UNREGISTER_RECIPE && ItemHolder.getTemplate(recipe.getKey()).getItemType() == EtcItemType.RECIPE) {
                    Recipe rp = RecipeHolder.getInstance().getRecipeByRecipeItem(recipe.getKey());
                    if (activeChar.hasRecipe(rp))
                        continue;
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfo(activeChar, recipeList, 0));
                    return;
                }

                ItemInstance item = activeChar.getInventory().getItemByItemId(recipe.getKey());
                if (item == null || item.getCount() < recipe.getValue()) {
                    activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_MATERIALS_TO_PERFORM_THAT_ACTION, new RecipeItemMakeInfo(activeChar, recipeList, 0));
                    return;
                }
            }

            for (Pair<Integer, Integer> recipe : recipes)
                if (recipe.getValue() != 0)
                    if (Config.ALT_GAME_UNREGISTER_RECIPE && ItemHolder.getTemplate(recipe.getKey()).getItemType() == EtcItemType.RECIPE)
                        activeChar.unregisterRecipe(RecipeHolder.getInstance().getRecipeByRecipeItem(recipe.getKey()).id);
                    else {
                        if (!activeChar.getInventory().destroyItemByItemId(recipe.getKey(), recipe.getValue(), "RecipeMakeSelf"))
                            continue;//TODO audit
                        activeChar.sendPacket(SystemMessage2.removeItems(recipe.getKey(), recipe.getValue()));
                    }
        } finally {
            activeChar.getInventory().writeUnlock();
        }

        activeChar.resetWaitSitTime();
        activeChar.reduceCurrentMp(recipeList.mpCost, null);

        int tryCount = 1, success = 0;
        if (Rnd.chance(Config.CRAFT_DOUBLECRAFT_CHANCE))
            tryCount++;

        for (int i = 0; i < tryCount; i++)
            if (Rnd.chance(recipeList.successRate)) {
                int itemId = recipeList.foundation != 0 ? Rnd.chance(Config.CRAFT_MASTERWORK_CHANCE) ? recipeList.foundation : recipeList.itemId : recipeList.itemId;
                long count = recipeList.count;
                ItemFunctions.addItem(activeChar, itemId, count, "RecipeMakeSelf");

                if (itemId == recipeList.foundation)
                    activeChar.getCounters().foundationItemsMade++;

                success = 1;
            }

        if (success == 0) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_FAILED_TO_MANUFACTURE_S1).addItemName(recipeList.itemId));
        }
        activeChar.sendPacket(new RecipeItemMakeInfo(activeChar, recipeList, success));
    }
}