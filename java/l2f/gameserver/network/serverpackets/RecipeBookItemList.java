package l2f.gameserver.network.serverpackets;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.Recipe;

import java.util.Collection;


public class RecipeBookItemList extends L2GameServerPacket {
    private final boolean _isDwarvenCraft;
    private final int _currentMp;
    private Collection<Recipe> _recipes;

    public RecipeBookItemList(Player player, boolean isDwarvenCraft) {
        _isDwarvenCraft = isDwarvenCraft;
        _currentMp = (int) player.getCurrentMp();
        if (isDwarvenCraft)
            _recipes = player.getDwarvenRecipeBook();
        else
            _recipes = player.getCommonRecipeBook();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xdc);
        writeD(_isDwarvenCraft ? 0x00 : 0x01);
        writeD(_currentMp);

        writeD(_recipes.size());

        for (Recipe recipe : _recipes) {
            writeD(recipe.getId());
            writeD(1); //??
        }
    }
}