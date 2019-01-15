package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.EnchantItemHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.WarehouseInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.network.serverpackets.EnchantResult;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.support.EnchantScroll;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Log;

public final class RequestEnchantItem extends L2GameClientPacket {
    private int _objectId, _catalystObjId;

    private static void showEnchantAnimation(Player player, int enchantLevel) {
        enchantLevel = Math.min(enchantLevel, 20);
        final int skillId = 23096 + enchantLevel;
        final MagicSkillUse msu = new MagicSkillUse(player, skillId);
        player.broadcastPacket(msu);
    }

    private static void doEnchantOld(Player player, ItemInstance item, ItemInstance scroll, ItemInstance catalyst) {
        PcInventory inventory = player.getInventory();

        if (!ItemFunctions.checkCatalyst(item, catalyst))
            catalyst = null;

        if (!item.canBeEnchanted(true)) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
            player.sendActionFailed();
            return;
        }

        int crystalId = ItemFunctions.getEnchantCrystalId(item, scroll, catalyst);

        if (crystalId == -1) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendActionFailed();
            return;
        }

        int scrollId = scroll.getItemId();

        if (scrollId == 13540 && item.getItemId() != 13539) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendActionFailed();
            return;
        }

        // ольф 21580(21581/21582)
        if ((scrollId == 21581 || scrollId == 21582) && item.getItemId() != 21580) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendActionFailed();
            return;
        }

        // ольф 21580(21581/21582)
        if ((scrollId != 21581 || scrollId != 21582) && item.getItemId() == 21580) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendActionFailed();
            return;
        }

        if (ItemFunctions.isDestructionWpnEnchantScroll(scrollId) && item.getEnchantLevel() >= 15 || ItemFunctions.isDestructionArmEnchantScroll(scrollId) && item.getEnchantLevel() >= 6) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.sendActionFailed();
            return;
        }

        int itemType = item.getTemplate().getType2();
        boolean fail = false;

        switch (item.getItemId()) {
            case 13539:
                if (item.getEnchantLevel() >= Config.ENCHANT_MAX_MASTER_YOGI_STAFF)
                    fail = true;
                break;
            case 21580:
                fail = item.getEnchantLevel() >= 9;
                break;
            default: {
                if (itemType == ItemTemplate.TYPE2_WEAPON) {
                    if (Config.ENCHANT_MAX_WEAPON > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_WEAPON)
                        fail = true;
                } else if (itemType == ItemTemplate.TYPE2_SHIELD_ARMOR) {
                    if (Config.ENCHANT_MAX_ARMOR > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_ARMOR)
                        fail = true;
                } else if (itemType == ItemTemplate.TYPE2_ACCESSORY) {
                    if (Config.ENCHANT_MAX_JEWELRY > 0 && item.getEnchantLevel() >= Config.ENCHANT_MAX_JEWELRY)
                        fail = true;
                } else fail = true;
            }
            break;
        }

        if (!inventory.destroyItem(scroll, 1L, "Enchanting") || catalyst != null && !inventory.destroyItem(catalyst, 1L, "Enchanting")) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendActionFailed();
            return;
        }

        if (fail) {
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
            player.sendActionFailed();
            return;
        }

        double chance = 100;

        if (ItemFunctions.isItemMallEnchantScroll(scrollId)) // Item Mall normal/ancient
            chance += 10;

        if (catalyst != null)
            chance += ItemFunctions.getCatalystPower(catalyst.getItemId());

        if (scrollId == 13540)
            chance = item.getEnchantLevel() < Config.SAFE_ENCHANT_MASTER_YOGI_STAFF ? 100 : Config.ENCHANT_CHANCE_MASTER_YOGI_STAFF;

        boolean equipped;

        if (equipped = item.isEquipped())
            inventory.unEquipItem(item);

        boolean isBlessedScroll = ItemFunctions.isBlessedEnchantScroll(scrollId);
        boolean isCrystalScroll = ItemFunctions.isCrystallEnchantScroll(scrollId);
        if (Rnd.chance(chance)) {
            if (isBlessedScroll)
                player.getCounters().enchantBlessedSucceeded++;
            else if (!isCrystalScroll)
                player.getCounters().enchantNormalSucceeded++;

            item.setEnchantLevel(item.getEnchantLevel() + 1);
            item.setJdbcState(JdbcEntityState.UPDATED);
            item.update();

            if (equipped)
                inventory.equipItem(item);

            player.sendPacket(new InventoryUpdate().addModifiedItem(item));

            player.sendPacket(EnchantResult.SUCESS);

            Log.LogAddItem(player, "EnchantSuccess", item, 1L);

            if (scrollId == 13540 && item.getEnchantLevel() > 3 || Config.SHOW_ENCHANT_EFFECT_RESULT) {
                showEnchantAnimation(player, item.getEnchantLevel());
            }
        } else if (ItemFunctions.isBlessedEnchantScroll(scrollId)) {
            item.setEnchantLevel(Config.SAFE_ENCHANT_LVL);
            item.setJdbcState(JdbcEntityState.UPDATED);
            item.update();

            if (equipped)
                inventory.equipItem(item);

            player.sendPacket(new InventoryUpdate().addModifiedItem(item));
            player.sendPacket(SystemMsg.THE_BLESSED_ENCHANT_FAILED);
            player.sendPacket(EnchantResult.BLESSED_FAILED);
            showEnchantAnimation(player, 0);

            Log.LogAddItem(player, "EnchantBlessedFail", item, 1L);
        } else if (ItemFunctions.isAncientEnchantScroll(scrollId) || ItemFunctions.isDestructionWpnEnchantScroll(scrollId) || ItemFunctions.isDestructionArmEnchantScroll(scrollId)) {
            player.sendPacket(EnchantResult.ANCIENT_FAILED);
            Log.LogAddItem(player, "EnchantDestructionFail", item, 1L);
        } else {
            if (item.isEquipped())
                player.sendDisarmMessage(item);

            if (!inventory.destroyItem(item, 1L, "EnchantFail")) {
                player.sendPacket(new SystemMessage(SystemMessage.THE_ENCHANTMENT_HAS_FAILED_YOUR_S1_HAS_BEEN_CRYSTALLIZED).addItemName(item.getItemId()));
                showEnchantAnimation(player, 0);
                player.sendActionFailed();
                return;
            }

            if (crystalId > 0 && item.getTemplate().getCrystalCount() > 0) {
                int crystalAmount = (int) (item.getTemplate().getCrystalCount() * 0.87);

                if (item.getEnchantLevel() > 3)
                    crystalAmount += item.getTemplate().getCrystalCount() * 0.25 * (item.getEnchantLevel() - 3);

                if (crystalAmount < 1)
                    crystalAmount = 1;

                player.sendPacket(new EnchantResult(1, crystalId, crystalAmount));
                ItemFunctions.addItem(player, crystalId, crystalAmount, true, "EnchantFailCrystals");
            } else player.sendPacket(EnchantResult.FAILED_NO_CRYSTALS);

            if (scrollId == 13540 || Config.SHOW_ENCHANT_EFFECT_RESULT) {
                showEnchantAnimation(player, 0);
            }
        }
    }

    @Override
    protected void readImpl() {
        _objectId = readD();
        _catalystObjId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (player.isActionsDisabled() || player.isBlocked()) {
            player.setEnchantScroll(null);
            player.sendActionFailed();
            return;
        }

        if (player.isInTrade()) {
            player.setEnchantScroll(null);
            player.sendActionFailed();
            return;
        }

        if (player.isSitting()) {
            player.setEnchantScroll(null);
            player.sendPacket(EnchantResult.CANCEL);
            player.sendMessage("You can't enchant while sitting.");
            player.sendActionFailed();
            return;
        }

        if (player.isInStoreMode()) {
            player.setEnchantScroll(null);
            player.sendPacket(EnchantResult.CANCEL);
            player.sendPacket(SystemMsg.YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP);
            player.sendActionFailed();
            return;
        }

        if (player.getAroundNpc(200, 200)
                .anyMatch(wh -> wh instanceof WarehouseInstance)) {
            player.sendMessage("You can't enchant near warehouse.");
            return;
        }

        PcInventory inventory = player.getInventory();
        inventory.writeLock();
        try {
            ItemInstance item = inventory.getItemByObjectId(_objectId);
            ItemInstance catalyst = _catalystObjId > 0 ? inventory.getItemByObjectId(_catalystObjId) : null;
            ItemInstance scroll = player.getEnchantScroll();

            if (item == null || scroll == null) {
                player.sendActionFailed();
                return;
            }

            EnchantScroll enchantScroll = EnchantItemHolder.getEnchantScroll(scroll.getItemId());
            if (enchantScroll == null) {
                doEnchantOld(player, item, scroll, catalyst);
                return;
            }

            if (enchantScroll.getMaxEnchant() != -1 && item.getEnchantLevel() >= enchantScroll.getMaxEnchant()) {
                player.sendPacket(EnchantResult.CANCEL);
                player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                player.sendActionFailed();
                return;
            }

            if (enchantScroll.getItems().size() > 0) {
                if (!enchantScroll.getItems().contains(item.getItemId())) {
                    player.sendPacket(EnchantResult.CANCEL);
                    player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
                    player.sendActionFailed();
                    return;
                }
            } else {
                if (!enchantScroll.getGrades().contains(item.getCrystalType())) {
                    player.sendPacket(EnchantResult.CANCEL);
                    player.sendPacket(SystemMsg.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
                    player.sendActionFailed();
                    return;
                }
            }

            if (!item.canBeEnchanted(false)) {
                player.sendPacket(EnchantResult.CANCEL);
                player.sendPacket(SystemMsg.INAPPROPRIATE_ENCHANT_CONDITIONS);
                player.sendActionFailed();
                return;
            }

            if (!inventory.destroyItem(scroll, 1L, "EnchantingItem") || catalyst != null && !inventory.destroyItem(catalyst, 1L, "EnchantingItem")) {
                player.sendPacket(EnchantResult.CANCEL);
                player.sendActionFailed();
                return;
            }

            boolean equipped;

            if (equipped = item.isEquipped())
                inventory.unEquipItem(item);

            int safeEnchantLevel = item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? 4 : 3;

            int chance = enchantScroll.getChance();

            if (item.getEnchantLevel() < safeEnchantLevel)
                chance = 100;


            if (Rnd.chance(chance)) {
                boolean isBlessedScroll = ItemFunctions.isBlessedEnchantScroll(enchantScroll.getItemId());
                boolean isCrystalScroll = ItemFunctions.isCrystallEnchantScroll(enchantScroll.getItemId());

                // success
                if (isBlessedScroll)
                    player.getCounters().enchantBlessedSucceeded++;
                else if (!isCrystalScroll)
                    player.getCounters().enchantNormalSucceeded++;

                item.setEnchantLevel(item.getEnchantLevel() + 1);
                Log.LogAddItem(player, "EnchantSuccess", item, 1L);
                item.setJdbcState(JdbcEntityState.UPDATED);
                item.update();

                if (equipped)
                    inventory.equipItem(item);

                if (item.getEnchantLevel() > player.getCounters().highestEnchant)
                    player.getCounters().highestEnchant = item.getEnchantLevel();

                player.sendPacket(new InventoryUpdate().addModifiedItem(item));

                player.sendPacket(EnchantResult.SUCESS);

                if (enchantScroll.isHasVisualEffect() && item.getEnchantLevel() > 3) {
                    showEnchantAnimation(player, item.getEnchantLevel());
                }
            } else {
                switch (enchantScroll.getResultType()) {
                    case CRYSTALS:
                        if (item.isEquipped())
                            player.sendDisarmMessage(item);

                        if (!inventory.destroyItem(item, 1L, "EnchantFail")) {
                            player.sendActionFailed();
                            return;
                        }

                        int crystalId = item.getCrystalType().cry;
                        if (crystalId > 0 && item.getTemplate().getCrystalCount() > 0) {
                            int crystalAmount = (int) (item.getTemplate().getCrystalCount() * 0.87);

                            if (item.getEnchantLevel() > 3)
                                crystalAmount += item.getTemplate().getCrystalCount() * 0.25 * (item.getEnchantLevel() - 3);

                            if (crystalAmount < 1)
                                crystalAmount = 1;

                            player.sendPacket(new EnchantResult(1, crystalId, crystalAmount));
                            ItemFunctions.addItem(player, crystalId, crystalAmount, true, "EnchantFailCrystals");
                        } else
                            player.sendPacket(EnchantResult.FAILED_NO_CRYSTALS);

                        if (enchantScroll.isHasVisualEffect()) {
                            showEnchantAnimation(player, 0);
                        }
                        break;
                    case DROP_ENCHANT:
                        item.setEnchantLevel(0);
                        item.setJdbcState(JdbcEntityState.UPDATED);
                        item.update();

                        if (equipped)
                            inventory.equipItem(item);

                        player.sendPacket(new InventoryUpdate().addModifiedItem(item));
                        player.sendPacket(SystemMsg.THE_BLESSED_ENCHANT_FAILED);
                        player.sendPacket(EnchantResult.BLESSED_FAILED);
                        showEnchantAnimation(player, 0);
                        break;
                    case NOTHING:
                        player.sendPacket(EnchantResult.ANCIENT_FAILED);
                        showEnchantAnimation(player, 0);
                        break;
                }

                // Alexander - Add a failed enchant to the stats
//				if (chance < 100)
//					player.addPlayerStats(Ranking.STAT_TOP_ENCHANTS_FAILED);
            }
        } finally {
            inventory.writeUnlock();

            player.setEnchantScroll(null);
            player.updateStats();
        }
    }
}