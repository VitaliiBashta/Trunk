package l2trunk.scripts.handler.items;

import l2trunk.commons.util.Rnd;
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
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

import java.util.List;

public final class SoulShots extends ScriptItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(
            5789, 1835, 1463, 1464, 1465, 1466, 1467, 13037, 13045, 13055, 22082, 22083, 22084, 22085, 22086);
    private static final List<Integer> _skillIds = List.of(2039, 2150, 2151, 2152, 2153, 2154);

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

        WeaponTemplate weaponItem = player.getActiveWeaponItem();

        ItemInstance weaponInst = player.getActiveWeaponInstance();
        int SoulshotId = item.getItemId();
        boolean isAutoSoulShot = false;

        if (player.getAutoSoulShot().contains(SoulshotId))
            isAutoSoulShot = true;

        if (weaponInst == null) {
            if (!isAutoSoulShot)
                player.sendPacket(Msg.CANNOT_USE_SOULSHOTS);
            return false;
        }

        // soulshot is already active
        if (weaponInst.getChargedSoulshot() != ItemInstance.CHARGED_NONE)
            return false;

        int grade = weaponItem.getCrystalType().externalOrdinal;
        int soulShotConsumption = weaponItem.getSoulShotCount();

        if (soulShotConsumption == 0) {
            // Can't use soulshots
            if (isAutoSoulShot) {
                player.removeAutoSoulShot(SoulshotId);
                player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(SoulshotId));
                return false;
            }
            player.sendPacket(Msg.CANNOT_USE_SOULSHOTS);
            return false;
        }

        if (grade == 0 && SoulshotId != 5789 && SoulshotId != 1835 // NG
                || grade == 1 && SoulshotId != 1463 && SoulshotId != 22082 && SoulshotId != 13037 // D
                || grade == 2 && SoulshotId != 1464 && SoulshotId != 22083 && SoulshotId != 13045 // C
                || grade == 3 && SoulshotId != 1465 && SoulshotId != 22084 // B
                || grade == 4 && SoulshotId != 1466 && SoulshotId != 22085 && SoulshotId != 13055 // A
                || grade == 5 && SoulshotId != 1467 && SoulshotId != 22086 // S
        ) {
            // wrong grade for weapon
            if (isAutoSoulShot)
                return false;
            player.sendPacket(Msg.SOULSHOT_DOES_NOT_MATCH_WEAPON_GRADE);
            return false;
        }

        if (weaponItem.getItemType() == WeaponType.BOW || weaponItem.getItemType() == WeaponType.CROSSBOW) {
            int newSS = (int) player.calcStat(Stats.SS_USE_BOW, soulShotConsumption);
            if (newSS < soulShotConsumption && Rnd.chance(player.calcStat(Stats.SS_USE_BOW_CHANCE, soulShotConsumption)))
                soulShotConsumption = newSS;
        }

        long count = item.getCount();

        if (Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY && count >= 1) {
            weaponInst.setChargedSoulshot(ItemInstance.CHARGED_SOULSHOT);
            player.sendPacket(Msg.POWER_OF_THE_SPIRITS_ENABLED);
            player.broadcastPacket(new MagicSkillUse(player, _skillIds.get(grade)));
        } else if (!Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY) {
            if (!player.getInventory().destroyItem(item, soulShotConsumption, null)) {
                player.sendPacket(Msg.NOT_ENOUGH_SOULSHOTS);
                return false;
            }
            weaponInst.setChargedSoulshot(ItemInstance.CHARGED_SOULSHOT);
            player.sendPacket(Msg.POWER_OF_THE_SPIRITS_ENABLED);
            player.broadcastPacket(new MagicSkillUse(player, _skillIds.get(grade)));
        }
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}