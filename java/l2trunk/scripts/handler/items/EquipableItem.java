package l2trunk.scripts.handler.items;

import l2trunk.gameserver.ai.PlayableAI.nextAction;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EquipableItem extends ScriptItemHandler implements ScriptFile {
    private final Set<Integer> _itemIds;

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    public EquipableItem() {
        Set<Integer> set = new HashSet<>();
        for (ItemTemplate template : ItemHolder.getInstance().getAllTemplates()) {
            if (template == null)
                continue;
            if (template.isEquipable())
                set.add(template.getItemId());
        }
        _itemIds = set;
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (!playable.isPlayer())
            return false;
        Player player = playable.getPlayer();
        if (player.isCastingNow()) {
            player.sendPacket(Msg.YOU_MAY_NOT_EQUIP_ITEMS_WHILE_CASTING_OR_PERFORMING_A_SKILL);
            return false;
        }

        // Нельзя снимать/одевать любое снаряжение при этих условиях
        if (player.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead() || player.isWeaponEquipBlocked()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }

        int bodyPart = item.getBodyPart();

        if (bodyPart == ItemTemplate.SLOT_LR_HAND || bodyPart == ItemTemplate.SLOT_L_HAND || bodyPart == ItemTemplate.SLOT_R_HAND) {
            // Нельзя снимать/одевать оружие, сидя на пете
            // Нельзя снимать/одевать проклятое оружие и флаги
            // Нельзя одевать/снимать оружие/щит/сигил, управляя кораблем
            if (player.isMounted() || player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null || player.isClanAirShipDriver()) {
                player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
                return false;
            }
        }

        // Нельзя снимать/одевать проклятое оружие
        if (item.isCursed()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }

        // Don't allow weapon/shield hero equipment during Olympiads
        if (player.isInOlympiadMode() && item.isHeroWeapon()) {
            player.sendActionFailed();
            return false;
        }

        if (player.isAttackingNow() || player.isCastingNow()) {
            player.getAI().setNextAction(nextAction.EQIP, item, null, ctrl, false);
            player.sendActionFailed();
            return false;
        }

        if (item.isEquipped()) {
            ItemInstance weapon = player.getActiveWeaponInstance();
            if (item == weapon) {
                player.abortAttack(true, true);
                player.abortCast(true, true);
            }
            player.sendDisarmMessage(item);
            player.getInventory().unEquipItem(item);
            return false;
        }

        L2GameServerPacket p = ItemFunctions.checkIfCanEquip(player, item);
        if (p != null) {
            player.sendPacket(p);
            return false;
        }

        player.getInventory().equipItem(item);
        if (!item.isEquipped()) {
            player.sendActionFailed();
            return false;
        }

        SystemMessage sm;
        if (item.getEnchantLevel() > 0) {
            sm = new SystemMessage(SystemMessage.EQUIPPED__S1_S2);
            sm.addNumber(item.getEnchantLevel());
            sm.addItemName(item.getItemId());
        } else
            sm = new SystemMessage(SystemMessage.YOU_HAVE_EQUIPPED_YOUR_S1).addItemName(item.getItemId());

        player.sendPacket(sm);
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return new ArrayList<>(_itemIds);
    }
}
