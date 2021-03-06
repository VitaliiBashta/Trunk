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

import java.util.List;

public final class BlessedSpiritShot extends ScriptItemHandler implements ScriptFile {
    // all the items ids that this handler knowns
    private static final List<Integer> ITEM_IDS = List.of(
            3947, 3948, 3949, 3950, 3951, 3952, 22072, 22073, 22074, 22075, 22076);
    private static final List<Integer> _skillIds = List.of(2061, 2160, 2161, 2162, 2163, 2164);


    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
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

        if (weaponInst.getChargedSpiritshot() == ItemInstance.CHARGED_BLESSED_SPIRITSHOT)
            // already charged by blessed spirit shot
            // btw we cant charge only when bsps is charged
            return false;

        int spiritshotId = item.getItemId();
        int grade = weaponItem.getCrystalType().externalOrdinal;
        int blessedsoulSpiritConsumption = weaponItem.getSpiritShotCount();

        if (blessedsoulSpiritConsumption == 0) {
            // Can't use Spiritshots
            if (isAutoSoulShot) {
                player.removeAutoSoulShot(SoulshotId);
                player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(spiritshotId));
                return false;
            }
            player.sendPacket(Msg.CANNOT_USE_SPIRITSHOTS);
            return false;
        }

        if (grade == 0 && spiritshotId != 3947 // NG
                || grade == 1 && spiritshotId != 3948 && spiritshotId != 22072 // D
                || grade == 2 && spiritshotId != 3949 && spiritshotId != 22073 // C
                || grade == 3 && spiritshotId != 3950 && spiritshotId != 22074 // B
                || grade == 4 && spiritshotId != 3951 && spiritshotId != 22075 // A
                || grade == 5 && spiritshotId != 3952 && spiritshotId != 22076 // S
        ) {
            if (isAutoSoulShot)
                return false;
            player.sendPacket(Msg.SPIRITSHOT_DOES_NOT_MATCH_WEAPON_GRADE);
            return false;
        }

        long count = item.getCount();
        if (Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY && count >= 1) {
            weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
            player.sendPacket(Msg.POWER_OF_MANA_ENABLED);
            player.broadcastPacket(new MagicSkillUse(player, _skillIds.get(grade)));
        } else if (!Config.ALLOW_SOUL_SPIRIT_SHOT_INFINITELY) {
            if (!player.getInventory().destroyItem(item, blessedsoulSpiritConsumption, null)) {
                if (isAutoSoulShot) {
                    player.removeAutoSoulShot(SoulshotId);
                    player.sendPacket(new ExAutoSoulShot(SoulshotId, false), new SystemMessage(SystemMessage.THE_AUTOMATIC_USE_OF_S1_WILL_NOW_BE_CANCELLED).addItemName(spiritshotId));
                    return false;
                }
                player.sendPacket(Msg.NOT_ENOUGH_SPIRITSHOTS);
                return false;
            }

            weaponInst.setChargedSpiritshot(ItemInstance.CHARGED_BLESSED_SPIRITSHOT);
            player.sendPacket(Msg.POWER_OF_MANA_ENABLED);
            player.broadcastPacket(new MagicSkillUse(player, _skillIds.get(grade)));
        }
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}