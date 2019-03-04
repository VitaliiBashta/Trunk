package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Recipe;

import java.util.Collection;


public final class RecipeBookItemList extends L2GameServerPacket {
    private final boolean isDwarvenCraft;
    private final int currentMp;
    private final Collection<Recipe> recipes;

    public RecipeBookItemList(Player player, boolean isDwarvenCraft) {
        this.isDwarvenCraft = isDwarvenCraft;
        currentMp = (int) player.getCurrentMp();
        if (isDwarvenCraft)
            recipes = player.getDwarvenRecipeBook();
        else
            recipes = player.getCommonRecipeBook();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xdc);
        writeD(isDwarvenCraft ? 0x00 : 0x01);
        writeD(currentMp);

        writeD(recipes.size());
        recipes.forEach(recipe -> {
            writeD(recipe.id);
            writeD(1); //??
        });
    }
}