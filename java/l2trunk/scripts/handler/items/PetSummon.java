package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.SkillTable;

import java.util.List;

public final class PetSummon extends ScriptItemHandler implements ScriptFile {
    private final Skill wolvesNecklace = SkillTable.INSTANCE.getInfo(2046);
    // all the items ids that this handler knowns
    private static final List<Integer> ITEM_IDS = PetDataTable.getPetControlItems();

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = (Player) playable;

        player.setPetControlItem(item);
        player.getAI().cast(wolvesNecklace, player, false, true);
        return true;
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
    public final List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}