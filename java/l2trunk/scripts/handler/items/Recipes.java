package l2trunk.scripts.handler.items;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.RecipeHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Recipe;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.RecipeBookItemList;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;
import java.util.stream.Collectors;

public final class Recipes extends ScriptItemHandler implements ScriptFile {
    private final List<Integer> ITEM_IDS;

    public Recipes() {
        ITEM_IDS = RecipeHolder.getInstance().getRecipes()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = (Player) playable;

        Recipe rp = RecipeHolder.getInstance().getRecipeByRecipeItem(item.getItemId());
        if (rp.isDwarvenRecipe()) {
            if (player.getDwarvenRecipeLimit() > 0) {
                if (player.getDwarvenRecipeBook().size() >= player.getDwarvenRecipeLimit()) {
                    player.sendPacket(Msg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
                    return false;
                }

                if (rp.getLevel() > player.getSkillLevel(Skill.SKILL_CRAFTING)) {
                    player.sendPacket(Msg.CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE);
                    return false;
                }
                if (player.hasRecipe(rp)) {
                    player.sendPacket(Msg.THAT_RECIPE_IS_ALREADY_REGISTERED);
                    return false;
                }
                if (!player.getInventory().destroyItem(item, 1L, "Recipes")) {
                    player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                    return false;
                }
                // add recipe to recipebook
                player.registerRecipe(rp, true);
                player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
                player.sendPacket(new RecipeBookItemList(player, true));
                return true;
            } else
                player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
        } else if (player.getCommonRecipeLimit() > 0) {
            if (player.getCommonRecipeBook().size() >= player.getCommonRecipeLimit()) {
                player.sendPacket(Msg.NO_FURTHER_RECIPES_MAY_BE_REGISTERED);
                return false;
            }
            if (player.hasRecipe(rp)) {
                player.sendPacket(Msg.THAT_RECIPE_IS_ALREADY_REGISTERED);
                return false;
            }
            if (!player.getInventory().destroyItem(item, 1L, "Recipes")) {
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                return false;
            }
            player.registerRecipe(rp, true);
            player.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_ADDED).addItemName(item.getItemId()));
            player.sendPacket(new RecipeBookItemList(player, false));
            return true;
        } else
            player.sendPacket(Msg.YOU_ARE_NOT_AUTHORIZED_TO_REGISTER_A_RECIPE);
        return false;
    }

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}