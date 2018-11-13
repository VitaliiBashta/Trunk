package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.SkillTable;

import java.util.List;

public final class PetSummon extends ScriptItemHandler implements ScriptFile {
    // all the items ids that this handler knowns
    private final List<Integer> _itemIds = PetDataTable.getPetControlItems();
    private static final int _skillId = 2046;

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = (Player) playable;

        player.setPetControlItem(item);
        player.getAI().Cast(SkillTable.getInstance().getInfo(_skillId, 1), player, false, true);
        return true;
    }

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public final List<Integer> getItemIds() {
        return _itemIds;
    }
}