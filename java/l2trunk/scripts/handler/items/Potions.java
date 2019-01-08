package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Potions extends SimpleItemHandler implements ScriptFile {
    private static final Map<Integer, Integer> ITEM_SKILL = new HashMap<>();


    static {
        ITEM_SKILL.put(7906, 2248);    //Blessing of Fire
        ITEM_SKILL.put(7907, 2249);    //Blessing of Water
        ITEM_SKILL.put(7908, 2250);    //Blessing of Wind
        ITEM_SKILL.put(7909, 2251);    //Blessing of Earth
        ITEM_SKILL.put(7910, 2252);    //Blessing of Darkness
        ITEM_SKILL.put(7911, 2253);    //Blessing of Sanctity
        ITEM_SKILL.put(9997, 2235);    //Fire Resist Potion
        ITEM_SKILL.put(9998, 2336);    //Water Resist Potion
        ITEM_SKILL.put(9999, 2338);    //Earth Resist Potion
        ITEM_SKILL.put(10000, 2337);   //Wind Resist Potion
        ITEM_SKILL.put(10001, 2340);   //Darkness Resist Potion
        ITEM_SKILL.put(10002, 2339);   //Holy Resist Potion
        ITEM_SKILL.put(14612, 23017);  // Christmas Red Sock
    }

    @Override
    public List<Integer> getItemIds() {
        return new ArrayList<>(ITEM_SKILL.keySet());
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
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        if (player.isInOlympiadMode()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
            return false;
        }

        if (player.isOutOfControl() || player.isDead() || player.isStunned() || player.isSleeping() || player.isParalyzed()) {
            return false;
        }

        if (!useItem(player, item, 1))
            return false;
        Integer skillId = ITEM_SKILL.get(itemId);
        if (skillId == null)
            return false;
        player.broadcastPacket(new MagicSkillUse(player, skillId));
        player.altOnMagicUseTimer(player, skillId);
        return true;
    }
}
