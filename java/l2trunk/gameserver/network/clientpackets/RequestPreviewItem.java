package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.BuyListHolder;
import l2trunk.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.network.serverpackets.ShopPreviewInfo;
import l2trunk.gameserver.network.serverpackets.ShopPreviewList;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RequestPreviewItem extends L2GameClientPacket {
    // format: cdddb

    private int listId;
    private int count;
    private List<Integer> items = new ArrayList<>();

    @Override
    protected void readImpl() {
        int _unknow = readD();
        listId = readD();
        count = readD();
        if (count * 4 > buf.remaining() || count > Short.MAX_VALUE || count < 1) {
            count = 0;
            return;
        }
        for (int i = 0; i < count; i++)
            items.add(readD());
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || count == 0)
            return;

        if (activeChar.isActionsDisabled()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && activeChar.getKarma() > 0 && !activeChar.isGM()) {
            activeChar.sendActionFailed();
            return;
        }

        NpcInstance merchant = activeChar.getLastNpc();
        boolean isValidMerchant = merchant != null && merchant.isMerchantNpc();
        if (!activeChar.isGM() && (!isValidMerchant || !activeChar.isInRange(merchant, Creature.INTERACTION_DISTANCE))) {
            activeChar.sendActionFailed();
            return;
        }

        NpcTradeList list = BuyListHolder.INSTANCE.getBuyList(listId);
        if (list == null) {
            //TODO audit
            activeChar.sendActionFailed();
            return;
        }

        long totalPrice = 0; // Цена на примерку каждого итема 10 Adena.

        Map<Integer, Integer> itemList = new HashMap<>();
        try {
            for (Integer item : items) {
                if (list.getItemByItemId(item) == null) {
                    activeChar.sendActionFailed();
                    return;
                }

                ItemTemplate template = ItemHolder.getTemplate(item);

                if (!template.isEquipable())
                    continue;

                int paperdoll = Inventory.getPaperdollIndex(template.getBodyPart());
                if (paperdoll < 0)
                    continue;

                if (activeChar.getRace() == Race.kamael) {
                    if (template.getItemType() == ArmorType.HEAVY || template.getItemType() == ArmorType.MAGIC || template.getItemType() == ArmorType.SIGIL || template.getItemType() == WeaponType.NONE)
                        continue;
                } else {
                    if (template.getItemType() == WeaponType.CROSSBOW || template.getItemType() == WeaponType.RAPIER || template.getItemType() == WeaponType.ANCIENTSWORD)
                        continue;
                }

                if (itemList.containsKey(paperdoll)) {
                    activeChar.sendPacket(SystemMsg.YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME);
                    return;
                } else
                    itemList.put(paperdoll, item);

                totalPrice += ShopPreviewList.getWearPrice(template);
            }

            if (!activeChar.reduceAdena(totalPrice, "ItemPreview")) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }
        } catch (ArithmeticException ae) {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        }

        if (!itemList.isEmpty()) {
            activeChar.sendPacket(new ShopPreviewInfo(itemList));
            // Schedule task
            ThreadPoolManager.INSTANCE.schedule(() -> {
                activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT_);
                activeChar.sendUserInfo(true);
            }, Config.WEAR_DELAY * 1000);
        }
    }

}