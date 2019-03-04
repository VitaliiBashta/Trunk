package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Recipe;
import l2trunk.gameserver.model.items.ManufactureItem;

import java.util.Collection;
import java.util.List;


public final class RecipeShopManageList extends L2GameServerPacket {
    private final List<ManufactureItem> createList;
    private final Collection<Recipe> recipes;
    private final int sellerId;
    private final long adena;
    private final boolean isDwarven;

    public RecipeShopManageList(Player seller, boolean isDwarvenCraft) {
        sellerId = seller.objectId();
        adena = seller.getAdena();
        isDwarven = isDwarvenCraft;
        if (isDwarven)
            recipes = seller.getDwarvenRecipeBook();
        else
            recipes = seller.getCommonRecipeBook();
        createList = seller.getCreateList();
        for (ManufactureItem mi : createList) {
            if (!seller.findRecipe(mi.getRecipeId()))
                createList.remove(mi);
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xde);
        writeD(sellerId);
        writeD((int) Math.min(adena, Integer.MAX_VALUE)); //FIXME не менять на writeQ, в текущем клиенте там все еще D (видимо баг NCSoft)
        writeD(isDwarven ? 0x00 : 0x01);
        writeD(recipes.size());
        int i = 1;
        for (Recipe recipe : recipes) {
            writeD(recipe.id);
            writeD(i++);
        }
        writeD(createList.size());
        createList.forEach(mi -> {
            writeD(mi.getRecipeId());
            writeD(0x00); //??
            writeQ(mi.getCost());
        });
    }
}