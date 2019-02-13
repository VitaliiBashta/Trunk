package l2trunk.gameserver.utils;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.CursedWeaponsManager;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import l2trunk.gameserver.model.items.attachment.PickableAttachment;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;

import java.util.stream.Stream;

public final class ItemFunctions {
    private static final Integer[][] catalyst = {
            // enchant catalyst list
            {12362, 14078, 14702}, // 0 - W D
            {12363, 14079, 14703}, // 1 - W C
            {12364, 14080, 14704}, // 2 - W B
            {12365, 14081, 14705}, // 3 - W A
            {12366, 14082, 14706}, // 4 - W S
            {12367, 14083, 14707}, // 5 - A D
            {12368, 14084, 14708}, // 6 - A C
            {12369, 14085, 14709}, // 7 - A B
            {12370, 14086, 14710}, // 8 - A A
            {12371, 14087, 14711}, // 9 - A S
    };

    private ItemFunctions() {
    }

    public static ItemInstance createItem(int itemId) {
        ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
        item.setLocation(ItemLocation.VOID);
        item.setCount(1L);

        return item;
    }

    public static void addItem(Player player, int itemId, long count) {
        addItem(player, itemId, count, "");
    }

    public static void addItem(Player player, int itemId, long count, String log) {
        ItemTemplate t = ItemHolder.getTemplate(itemId);
        if (t.stackable())
            player.inventory.addItem(itemId, count, log);
        else
            for (int i = 0; i < count; i++)
                player.inventory.addItem(itemId, 1, log);

        player.sendPacket(SystemMessage2.obtainItems(itemId, count, 0));
    }


//    public static long removeItem(Player player, int itemId, long count, String log) {
//        return removeItem(player, itemId, count, log);
//    }

    public static long removeItem(Player player, int itemId, long count, String log) {
        long removed = 0;
        if (player == null || count < 1)
            return removed;

        ItemTemplate t = ItemHolder.getTemplate(itemId);
        if (t.stackable()) {
            if (player.inventory.destroyItemByItemId(itemId, count, log))
                removed = count;
        } else
            for (long i = 0; i < count; i++)
                if (player.inventory.destroyItemByItemId(itemId, log))
                    removed++;

        if (removed > 0 )
            player.sendPacket(SystemMessage2.removeItems(itemId, removed));

        return removed;
    }

    public static SystemMessage2 checkIfCanEquip(PetInstance pet, ItemInstance item) {
        if (!item.isEquipable())
            return new SystemMessage2(SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM);

        int petId = pet.getNpcId();

        if (item.getTemplate().isPendant() //
                || PetDataTable.isWolf(petId) && item.getTemplate().isForWolf() //
                || PetDataTable.isHatchling(petId) && item.getTemplate().isForHatchling() //
                || PetDataTable.isStrider(petId) && item.getTemplate().isForStrider() //
                || PetDataTable.isGWolf(petId) && item.getTemplate().isForGWolf() //
                || PetDataTable.isBabyPet(petId) && item.getTemplate().isForPetBaby() //
                || PetDataTable.isImprovedBabyPet(petId) && item.getTemplate().isForPetBaby() //
        )
            return null;

        return new SystemMessage2(SystemMsg.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
    }

    public static L2GameServerPacket checkIfCanEquip(Player player, ItemInstance item) {
        int itemId = item.getItemId();
        int targetSlot = item.getTemplate().getBodyPart();
        Clan clan = player.getClan();

        // Heroic weapons and Wings of Destiny Circlet and Cloak of Hero
        if ((item.isHeroWeapon() || item.getItemId() == 6842 || item.getItemId() == 37032) && !player.isHero() && !player.isGM())
            return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

        if (player.getRace() == Race.kamael && (item.getItemType() == ArmorType.HEAVY || item.getItemType() == ArmorType.MAGIC || item.getItemType() == ArmorType.SIGIL || item.getItemType() == WeaponType.NONE))
            return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

        if (player.getRace() != Race.kamael && (item.getItemType() == WeaponType.CROSSBOW || item.getItemType() == WeaponType.RAPIER || item.getItemType() == WeaponType.ANCIENTSWORD))
            return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

        if (itemId >= 7850 && itemId <= 7859 && player.getLvlJoinedAcademy() == 0) // Clan Oath Armor
            return new SystemMessage2(SystemMsg.THIS_ITEM_CAN_ONLY_BE_WORN_BY_A_MEMBER_OF_THE_CLAN_ACADEMY);

        if (item.getItemType() == WeaponType.DUALDAGGER && player.getSkillLevel(923) < 1)
            return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

        if (ItemTemplate.ITEM_ID_CASTLE_CIRCLET.contains(itemId) && (clan == null || itemId != ItemTemplate.ITEM_ID_CASTLE_CIRCLET.get(clan.getCastle())))
            return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

        // Custom Cloaks of Aden / Dion / Giran / Gludio / Goddard / Innadril / Oren / Rune / Schuttgart
        if (ItemTemplate.ITEM_ID_CASTLE_CLOAK.contains(itemId) && (clan == null || itemId != ItemTemplate.ITEM_ID_CASTLE_CLOAK.get(clan.getCastle())))
            return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

        if (itemId == 6841 && (clan == null || !player.isClanLeader() || clan.getCastle() == 0))
            return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

        if (targetSlot == ItemTemplate.SLOT_LR_HAND || targetSlot == ItemTemplate.SLOT_L_HAND || targetSlot == ItemTemplate.SLOT_R_HAND) {
            if (itemId != player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) && CursedWeaponsManager.INSTANCE.isCursed(player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND)))
                return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
            if (player.isCursedWeaponEquipped() && itemId != player.getCursedWeaponEquippedId())
                return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
        }

        if (item.getTemplate().isCloak()) {
            // Can be worn by Knights or higher ranks who own castle
            if (item.getName().contains("Knight") && (player.getPledgeClass() < Player.RANK_KNIGHT || player.getCastle() == null))
                return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

            if (item.getName().contains("Kamael") && player.getRace() != Race.kamael)
                return new SystemMessage2(SystemMsg.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);

            if (!player.getOpenCloak())
                return new SystemMessage2(SystemMsg.THE_CLOAK_CANNOT_BE_EQUIPPED_BECAUSE_A_NECESSARY_ITEM_IS_NOT_EQUIPPED);
        }

        if (targetSlot == ItemTemplate.SLOT_DECO) {
            int count = player.getTalismanCount();
            if (count <= 0)
                return new SystemMessage2(SystemMsg.YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET).addItemName(itemId);

            ItemInstance deco;
            for (int slot = Inventory.PAPERDOLL_DECO1; slot <= Inventory.PAPERDOLL_DECO6; slot++) {
                deco = player.getInventory().getPaperdollItem(slot);
                if (deco != null) {
                    if (deco == item)
                        return null;
                    if (--count <= 0 || deco.getItemId() == itemId)
                        return new SystemMessage2(SystemMsg.YOU_CANNOT_EQUIP_S1_BECAUSE_YOU_DO_NOT_HAVE_ANY_AVAILABLE_SLOTS).addItemName(itemId);
                }
            }
        }
        return null;
    }

    public static boolean checkIfCanPickup(Playable playable, ItemInstance item) {
        Player player = playable.getPlayer();
        return item.getDropTimeOwner() <= System.currentTimeMillis() || item.getDropPlayers().contains(player.objectId());
    }

    public static boolean canAddItem(Player player, ItemInstance item) {
        if (!player.getInventory().validateWeight(item)) {
            player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return false;
        }

        if (!player.getInventory().validateCapacity(item)) {
            player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
            return false;
        }

        if (!item.getTemplate().getHandler().pickupItem(player, item))
            return false;

        PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;
        return attachment == null || attachment.canPickUp(player);
    }

    public static boolean checkIfCanDiscard(Player player, ItemInstance item) {
        if (item.isHeroWeapon())
            return false;

        if (PetDataTable.isPetControlItem(item) && player.isMounted())
            return false;

        if (player.getPetControlItem() == item)
            return false;

        if (player.getEnchantScroll() == item)
            return false;

        if (item.isCursed())
            return false;

        if (item.getTemplate().isQuest())
            return false;

        return true;
    }

    /**
     * Enchant
     *
     * @param itemId
     * @return
     */
    public static boolean isBlessedEnchantScroll(int itemId) {
        switch (itemId) {
            case 6575: // Wpn D
            case 6576: // Arm D
            case 6573: // Wpn C
            case 6574: // Arm C
            case 6571: // Wpn B
            case 6572: // Arm B
            case 6569: // Wpn A
            case 6570: // Arm A
            case 6577: // Wpn S
            case 6578: // Arm S
            case 21582: // Blessed Enchant Scroll T'Shirt
                return true;
        }
        return false;
    }

    public static boolean isAncientEnchantScroll(int itemId) {
        switch (itemId) {
            case 22014: // Wpn B
            case 22016: // Arm B
            case 22015: // Wpn A
            case 22017: // Arm A
            case 20519: // Wpn S
            case 20520: // Arm S
                return true;
        }
        return false;
    }

    public static boolean isDestructionWpnEnchantScroll(int itemId) {
        switch (itemId) {
            case 22221:
            case 22223:
            case 22225:
            case 22227:
            case 22229:
                return true;
        }
        return false;
    }

    public static boolean isDestructionArmEnchantScroll(int itemId) {
        switch (itemId) {
            case 22222:
            case 22224:
            case 22226:
            case 22228:
            case 22230:
                return true;
        }
        return false;
    }

    public static boolean isItemMallEnchantScroll(int itemId) {
        switch (itemId) {
            case 22006: // Wpn D
            case 22010: // Arm D
            case 22007: // Wpn C
            case 22011: // Arm C
            case 22008: // Wpn B
            case 22012: // Arm B
            case 22009: // Wpn A
            case 22013: // Arm A
            case 20517: // Wpn S
            case 20518: // Arm S
                return true;
            default:
                return isAncientEnchantScroll(itemId);
        }
    }

    public static boolean isDivineEnchantScroll(int itemId) {
        switch (itemId) {
            case 22018: // Wpn B
            case 22020: // Arm B
            case 22019: // Wpn A
            case 22021: // Arm A
            case 20521: // Wpn S
            case 20522: // Arm S
                return true;
        }
        return false;
    }

    public static boolean isCrystallEnchantScroll(int itemId) {
        switch (itemId) {
            case 957: // Wpn D
            case 958: // Arm D
            case 953: // Wpn C
            case 954: // Arm C
            case 949: // Wpn B
            case 950: // Arm B
            case 731: // Wpn A
            case 732: // Arm A
            case 961: // Wpn S
            case 962: // Arm S
                return true;
        }
        return false;
    }

    public static int getEnchantCrystalId(ItemInstance item, ItemInstance scroll, ItemInstance catalyst) {
        boolean scrollValid, catalystValid = false;

        scrollValid = getEnchantScrollId(item)
                .anyMatch(scrollId -> scroll.getItemId() == scrollId);


        if (catalyst == null)
            catalystValid = true;
        else
            catalystValid = getEnchantCatalystId(item)
                    .anyMatch(catalystId -> catalystId == catalyst.getItemId());

        if (scrollValid && catalystValid)
            switch (item.getCrystalType().cry) {
                case ItemTemplate.CRYSTAL_NONE:
                    return 0;
                case ItemTemplate.CRYSTAL_D:
                    return 1458;
                case ItemTemplate.CRYSTAL_C:
                    return 1459;
                case ItemTemplate.CRYSTAL_B:
                    return 1460;
                case ItemTemplate.CRYSTAL_A:
                    return 1461;
                case ItemTemplate.CRYSTAL_S:
                    return 1462;
            }

        return -1;
    }

    private static Stream<Integer> getEnchantScrollId(ItemInstance item) {
        if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
            switch (item.getCrystalType().cry) {
                case ItemTemplate.CRYSTAL_NONE:
                    return Stream.of(13540);
                case ItemTemplate.CRYSTAL_D:
                    return Stream.of(955, 6575, 957, 22006, 22229);
                case ItemTemplate.CRYSTAL_C:
                    return Stream.of(951, 6573, 953, 22007, 22227);
                case ItemTemplate.CRYSTAL_B:
                    return Stream.of(947, 6571, 949, 22008, 22014, 22018, 22225);
                case ItemTemplate.CRYSTAL_A:
                    return Stream.of(729, 6569, 731, 22009, 22015, 22019, 22223);
                case ItemTemplate.CRYSTAL_S:
                    return Stream.of(959, 6577, 961, 20517, 20519, 20521, 22221);
            }
        else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
            switch (item.getCrystalType().cry) {
                case ItemTemplate.CRYSTAL_NONE:
                    return Stream.of(21581, 21582);
                case ItemTemplate.CRYSTAL_D:
                    return Stream.of(956, 6576, 958, 22010, 22230);
                case ItemTemplate.CRYSTAL_C:
                    return Stream.of(952, 6574, 954, 22011, 22228);
                case ItemTemplate.CRYSTAL_B:
                    return Stream.of(948, 6572, 950, 22012, 22016, 22020, 22226);
                case ItemTemplate.CRYSTAL_A:
                    return Stream.of(730, 6570, 732, 22013, 22017, 22021, 22224);
                case ItemTemplate.CRYSTAL_S:
                    return Stream.of(960, 6578, 962, 20518, 20520, 20522, 22222);
            }
        return Stream.of();
    }

    private static Stream<Integer> getEnchantCatalystId(ItemInstance item) {
        if (item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
            switch (item.getCrystalType().cry) {
                case ItemTemplate.CRYSTAL_A:
                    return Stream.of(catalyst[3]);
                case ItemTemplate.CRYSTAL_B:
                    return Stream.of(catalyst[2]);
                case ItemTemplate.CRYSTAL_C:
                    return Stream.of(catalyst[1]);
                case ItemTemplate.CRYSTAL_D:
                    return Stream.of(catalyst[0]);
                case ItemTemplate.CRYSTAL_S:
                    return Stream.of(catalyst[4]);
            }
        else if (item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
            switch (item.getCrystalType().cry) {
                case ItemTemplate.CRYSTAL_A:
                    return Stream.of(catalyst[8]);
                case ItemTemplate.CRYSTAL_B:
                    return Stream.of(catalyst[7]);
                case ItemTemplate.CRYSTAL_C:
                    return Stream.of(catalyst[6]);
                case ItemTemplate.CRYSTAL_D:
                    return Stream.of(catalyst[5]);
                case ItemTemplate.CRYSTAL_S:
                    return Stream.of(catalyst[9]);
            }
        return Stream.of(0, 0, 0);
    }

    public static int getCatalystPower(int itemId) {
        for (int i = 0; i < catalyst.length; i++)
            for (int id : catalyst[i])
                if (id == itemId)
                    switch (i) {
                        case 0:
                            return 20;
                        case 1:
                            return 18;
                        case 2:
                            return 15;
                        case 3:
                            return 12;
                        case 4:
                            return 10;
                        case 5:
                            return 35;
                        case 6:
                            return 27;
                        case 7:
                            return 23;
                        case 8:
                            return 18;
                        case 9:
                            return 15;
                    }

        return 0;
    }

    public static boolean checkCatalyst(ItemInstance item, ItemInstance catalyst) {
        if (item == null || catalyst == null)
            return false;

        int current = item.getEnchantLevel();
        if (current < (item.getTemplate().getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR ? 4 : 3) || current > 8)
            return false;

        return getEnchantCatalystId(item)
                .anyMatch(id -> id == catalyst.getItemId());
    }

    public static boolean isLifeStone(int itemId) {
        return itemId >= 8723 && itemId <= 8762
                || itemId >= 9573 && itemId <= 9576
                || itemId >= 10483 && itemId <= 10486
                || itemId >= 14166 && itemId <= 14169
                || itemId >= 16160 && itemId <= 16167;
    }

    public static boolean isAccessoryLifeStone(int itemId) {
        return itemId >= 12754 && itemId <= 12763
                || itemId >= 12840 && itemId <= 12851
                || itemId == 12821 || itemId == 12822
                || itemId == 14008 || itemId == 16177
                || itemId == 16178;
    }

    public static int getLifeStoneGrade(int itemId) {
        switch (itemId) {
            case 8723:
            case 8724:
            case 8725:
            case 8726:
            case 8727:
            case 8728:
            case 8729:
            case 8730:
            case 8731:
            case 8732:
            case 9573:
            case 10483:
            case 14166:
            case 16160:
            case 16164:
                return 0;
            case 8733:
            case 8734:
            case 8735:
            case 8736:
            case 8737:
            case 8738:
            case 8739:
            case 8740:
            case 8741:
            case 8742:
            case 9574:
            case 10484:
            case 14167:
            case 16161:
            case 16165:
                return 1;
            case 8743:
            case 8744:
            case 8745:
            case 8746:
            case 8747:
            case 8748:
            case 8749:
            case 8750:
            case 8751:
            case 8752:
            case 9575:
            case 10485:
            case 14168:
            case 16162:
            case 16166:
                return 2;
            case 8753:
            case 8754:
            case 8755:
            case 8756:
            case 8757:
            case 8758:
            case 8759:
            case 8760:
            case 8761:
            case 8762:
            case 9576:
            case 10486:
            case 14169:
            case 16163:
            case 16167:
                return 3;
            default:
                return 0;
        }
    }

    public static int getLifeStoneLevel(int itemId) {
        switch (itemId) {
            case 8723:
            case 8733:
            case 8743:
            case 8753:
            case 12754:
            case 12840:
                return 1;
            case 8724:
            case 8734:
            case 8744:
            case 8754:
            case 12755:
            case 12841:
                return 2;
            case 8725:
            case 8735:
            case 8745:
            case 8755:
            case 12756:
            case 12842:
                return 3;
            case 8726:
            case 8736:
            case 8746:
            case 8756:
            case 12757:
            case 12843:
                return 4;
            case 8727:
            case 8737:
            case 8747:
            case 8757:
            case 12758:
            case 12844:
                return 5;
            case 8728:
            case 8738:
            case 8748:
            case 8758:
            case 12759:
            case 12845:
                return 6;
            case 8729:
            case 8739:
            case 8749:
            case 8759:
            case 12760:
            case 12846:
                return 7;
            case 8730:
            case 8740:
            case 8750:
            case 8760:
            case 12761:
            case 12847:
                return 8;
            case 8731:
            case 8741:
            case 8751:
            case 8761:
            case 12762:
            case 12848:
                return 9;
            case 8732:
            case 8742:
            case 8752:
            case 8762:
            case 12763:
            case 12849:
                return 10;
            case 9573:
            case 9574:
            case 9575:
            case 9576:
            case 12821:
            case 12850:
                return 11;
            case 10483:
            case 10484:
            case 10485:
            case 10486:
            case 12822:
            case 12851:
                return 12;
            case 14008:
            case 14166:
            case 14167:
            case 14168:
            case 14169:
                return 13;
            case 16160:
            case 16161:
            case 16162:
            case 16163:
            case 16177:
                return 14;
            case 16164:
            case 16165:
            case 16166:
            case 16167:
            case 16178:
                return 15;
            default:
                return 1;
        }
    }

    public static Element getEnchantAttributeStoneElement(int itemId, boolean isArmor) {
        Element element = Element.NONE;
        switch (itemId) {
            case 9546:
            case 9552:
            case 10521:
            case 9558:
            case 9564:
                element = Element.FIRE;
                break;
            case 9547:
            case 9553:
            case 10522:
            case 9559:
            case 9565:
                element = Element.WATER;
                break;
            case 9548:
            case 9554:
            case 10523:
            case 9560:
            case 9566:
                element = Element.EARTH;
                break;
            case 9549:
            case 9555:
            case 10524:
            case 9561:
            case 9567:
                element = Element.WIND;
                break;
            case 9550:
            case 9556:
            case 10525:
            case 9562:
            case 9568:
                element = Element.UNHOLY;
                break;
            case 9551:
            case 9557:
            case 10526:
            case 9563:
            case 9569:
                element = Element.HOLY;
                break;
        }

        if (isArmor)
            return Element.getReverseElement(element);

        return element;
    }
}
