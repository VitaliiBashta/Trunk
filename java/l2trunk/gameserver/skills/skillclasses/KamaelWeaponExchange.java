package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ExAutoSoulShot;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.ShortCutInit;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.WeaponTemplate;

import java.util.List;

public final class KamaelWeaponExchange extends Skill {
    public KamaelWeaponExchange(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player.isInStoreMode() || player.isProcessingRequest())
            return false;

        ItemInstance item = player.getActiveWeaponInstance();
        if (item != null && ((WeaponTemplate) item.getTemplate()).getKamaelConvert() == 0) {
            player.sendPacket(SystemMsg.YOU_CANNOT_CONVERT_THIS_ITEM);
            return false;
        }

        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        final Player player = (Player) activeChar;
        final ItemInstance item = player.getActiveWeaponInstance();
        if (item == null)
            return;

        int itemId = ((WeaponTemplate) item.getTemplate()).getKamaelConvert();

        if (itemId == 0)
            return;

        player.getInventory().unEquipItem(item);
        player.sendPacket(new InventoryUpdate().addRemovedItem(item));
        item.setItemId(itemId);

        player.sendPacket(new ShortCutInit(player));
        for (int shotId : player.getAutoSoulShot())
            player.sendPacket(new ExAutoSoulShot(shotId, true));

        player.sendPacket(new InventoryUpdate().addNewItem(item));
        player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EQUIPPED_YOUR_S1).addItemNameWithAugmentation(item));
        player.getInventory().equipItem(item);
    }
}