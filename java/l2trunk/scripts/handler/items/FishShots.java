package l2trunk.scripts.handler.items;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ExAutoSoulShot;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

import java.util.List;

public final class FishShots extends ScriptItemHandler implements ScriptFile {
    // All the item IDs that this handler knows.
    private static final List<Integer> ITEM_IDS = List.of(6535, 6536, 6537, 6538, 6539, 6540);
    private static final List<Integer> SKILL_IDS = List.of(2181, 2182, 2183, 2184, 2185, 2186);

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        int FishshotId = item.getItemId();

        boolean isAutoSoulShot = false;
        if (player.getAutoSoulShot().contains(FishshotId))
            isAutoSoulShot = true;

        ItemInstance weaponInst = player.getActiveWeaponInstance();
        WeaponTemplate weaponItem = player.getActiveWeaponItem();

        if (weaponInst == null || weaponItem.getItemType() != WeaponType.ROD) {
            if (!isAutoSoulShot)
                player.sendPacket(Msg.CANNOT_USE_SOULSHOTS);
            return false;
        }

        // spiritshot is already active
        if (weaponInst.getChargedFishshot())
            return false;

        if (item.getCount() < 1) {
            if (isAutoSoulShot) {
                player.removeAutoSoulShot(FishshotId);
                player.sendPacket(new ExAutoSoulShot(FishshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addString(item.getName()));
                return false;
            }
            player.sendPacket(Msg.NOT_ENOUGH_SPIRITSHOTS);
            return false;
        }

        int grade = weaponItem.getCrystalType().externalOrdinal;

        if (grade == 0 && FishshotId != 6535 || grade == 1 && FishshotId != 6536 || grade == 2 && FishshotId != 6537 || grade == 3 && FishshotId != 6538 || grade == 4 && FishshotId != 6539 || grade == 5 && FishshotId != 6540) {
            if (isAutoSoulShot)
                return false;
            player.sendPacket(Msg.THIS_FISHING_SHOT_IS_NOT_FIT_FOR_THE_FISHING_POLE_CRYSTAL);
            return false;
        }

        if (player.getInventory().destroyItem(item, 1L, "FishShots")) {
            weaponInst.setChargedFishshot(true);
            player.sendPacket(Msg.POWER_OF_MANA_ENABLED);
            player.broadcastPacket(new MagicSkillUse(player, SKILL_IDS.get(grade)));
        }
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}
