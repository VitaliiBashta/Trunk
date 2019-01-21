package l2trunk.gameserver.stats.conditions;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.stats.Env;

public final class ConditionSlotItemId extends ConditionInventory {
    private final int itemId;

    private final int enchantLevel;

    public ConditionSlotItemId(int slot, int itemId, int enchantLevel) {
        super(slot);
        this.itemId = itemId;
        this.enchantLevel = enchantLevel;
    }

    @Override
    protected boolean testImpl(Env env) {
        if (!env.character.isPlayer())
            return false;
        Inventory inv = ((Player) env.character).getInventory();
        ItemInstance item = inv.getPaperdollItem(slot);
        if (item == null)
            return itemId == 0;
        return item.getItemId() == itemId && item.getEnchantLevel() >= enchantLevel;
    }
}
