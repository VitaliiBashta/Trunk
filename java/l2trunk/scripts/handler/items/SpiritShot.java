package l2trunk.scripts.handler.items;

import l2trunk.gameserver.Config;
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

import java.util.Arrays;
import java.util.List;

public final class SpiritShot extends ScriptItemHandler implements ScriptFile {
    // all the items ids that this handler knowns
    private static final List<Integer> ITEM_IDS = List.of(5790, 2509, 2510, 2511, 2512, 2513, 2514, 22077, 22078, 22079, 22080, 22081);
    private static final List<Integer> SKILL_IDS = List.of(2061, 2155, 2156, 2157, 2158, 2159);

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

        ItemInstance weaponInst = player.getActiveWeaponInstance();
        WeaponTemplate weaponItem = player.getActiveWeaponItem();
        int SoulshotId = item.getItemId();
        boolean isAutoSoulShot = false;

        if (player.getAutoSoulShot().contains(SoulshotId))
            isAutoSoulShot = true;

        if (weaponInst == null) {
            if (!isAutoSoulShot)
                player.sendPacket(Msg.CANNOT_USE_SPIRITSHOTS);
            return false;
        }

        // spiritshot is already active
        if (weaponInst.getChargedSpiritshot() != ItemInstance.CHARGED_NONE)
            return false;

        int SpiritshotId = item.getItemId();
        int grade = weaponItem.getCrystalType().externalOrdinal;
        int soulSpiritConsumption = weaponItem.getSpiritShotCount();
        long count = item.getCount();

        if (soulSpiritConsumption == 0) {
            // Can't use Spiritshots
            if (isAutoSoulShot) {
                player.removeAutoSoulShot(SoulshotId);
                player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(SoulshotId));
                return false;
            }
            player.sendPacket(Msg.CANNOT_USE_SPIRITSHOTS);
            return false;
        }

        if (grade == 0 && SpiritshotId != 5790 && SpiritshotId != 2509 // NG
                || grade == 1 && SpiritshotId != 2510 && SpiritshotId != 22077 // D
                || grade == 2 && SpiritshotId != 2511 && SpiritshotId != 22078 // C
                || grade == 3 && SpiritshotId != 2512 && SpiritshotId != 22079 // B
                || grade == 4 && SpiritshotId != 2513 && SpiritshotId != 22080 // A
                || grade == 5 && SpiritshotId != 2514 && SpiritshotId != 22081 // S
        ) {
            // wrong grade for weapon
            if (isAutoSoulShot)
                return false;
            player.sendPacket(Msg.SPIRITSHOT_DOES_NOT_MATCH_WEAPON_GRADE);
            return false;
        }

        if (count < soulSpiritConsumption) {
            if (isAutoSoulShot) {
                player.removeAutoSoulShot(SoulshotId);
                player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(SoulshotId));
                return false;
            }
            player.sendPacket(Msg.NOT_ENOUGH_SPIRITSHOTS);
            return false;
        }

        if (Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY) {
            weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_SPIRITSHOT);
            player.sendPacket(Msg.POWER_OF_MANA_ENABLED);
            player.broadcastPacket(new MagicSkillUse(player, SKILL_IDS.get(grade)));
        } else {
            if (player.getInventory().destroyItem(item, soulSpiritConsumption, null)) {
                weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_SPIRITSHOT);
                player.sendPacket(Msg.POWER_OF_MANA_ENABLED);
                player.broadcastPacket(new MagicSkillUse(player, SKILL_IDS.get(grade)));
            }
        }
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}