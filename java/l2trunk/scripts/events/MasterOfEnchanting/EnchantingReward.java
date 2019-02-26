package l2trunk.scripts.events.MasterOfEnchanting;

import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.Functions;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class EnchantingReward extends Functions {
    private static final int MASTER_YOGI_STAFF = 13539;
    private static final int MASTER_YOGI_SCROLL = 13540;

    private static final int STAFF_PRICE = 500000;
    private static final int TIMED_SCROLL_PRICE = 3000000;
    private static final int TIMED_SCROLL_HOURS = 6;

    private static final int ONE_SCROLL_PRICE = 250000;
    private static final int TEN_SCROLLS_PRICE = 2500000;

    private static int[] HAT_SHADOW = new int[]{13074, 13075, 13076};
    private static int[] HAT_EVENT = new int[]{13518, 13519, 13522};
    private static int[] SOUL_CRYSTALL = new int[]{9570, 9571, 9572};

    public void buy_staff() {
        if (!player.haveItem(MASTER_YOGI_STAFF) && player.haveAdena(STAFF_PRICE)) {
            player.reduceAdena(STAFF_PRICE, "MasterOfEnchanting");
            addItem(player, MASTER_YOGI_STAFF, 1, "Yogi");
            show("scripts/events/MasterOfEnchanting/32599-staffbuyed.htm", player);
        } else {
            show("scripts/events/MasterOfEnchanting/32599-staffcant.htm", player);
        }
    }

    public void buy_scroll_lim() {
        long reuseTime = TIMED_SCROLL_HOURS * 60 * 60 * 1000;
        long currTime = System.currentTimeMillis();
        long _remaining_time;
        if (player.isVarSet("MasterOfEnch"))
            _remaining_time = currTime - player.getVarLong("MasterOfEnch");
        else
            _remaining_time = reuseTime;
        if (_remaining_time >= reuseTime) {
            if (player.haveAdena(TIMED_SCROLL_PRICE)) {
                player.reduceAdena(TIMED_SCROLL_PRICE, "MasterOfEnchanting");
                addItem(player, MASTER_YOGI_SCROLL);
                player.setVar("MasterOfEnch", currTime);
                show("scripts/events/MasterOfEnchanting/32599-scroll24.htm", player);
            } else
                show("scripts/events/MasterOfEnchanting/32599-s24-no.htm", player);
        } else {
            int hours = (int) (reuseTime - _remaining_time) / 3600000;
            int minutes = (int) (reuseTime - _remaining_time) % 3600000 / 60000;
            if (hours > 0) {
                SystemMessage sm = new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED);
                sm.addNumber(hours);
                sm.addNumber(minutes);
                player.sendPacket(sm);
                show("scripts/events/MasterOfEnchanting/32599-scroll24.htm", player);
            } else if (minutes > 0) {
                SystemMessage sm = new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED);
                sm.addNumber(minutes);
                player.sendPacket(sm);
                show("scripts/events/MasterOfEnchanting/32599-scroll24.htm", player);
            } else if (player.getAdena() >= TIMED_SCROLL_PRICE) {
                player.reduceAdena(TIMED_SCROLL_PRICE, "MasterOfEnchanting");
                addItem(player, MASTER_YOGI_SCROLL, 1);
                player.setVar("MasterOfEnch", currTime);
                show("scripts/events/MasterOfEnchanting/32599-scroll24.htm", player);
            } else
                show("scripts/events/MasterOfEnchanting/32599-s24-no.htm", player);
        }
    }

    public void buy_scroll_1() {
        if (player.haveAdena(ONE_SCROLL_PRICE)) {
            player.reduceAdena(ONE_SCROLL_PRICE, "MasterOfEnchanting");
            addItem(player, MASTER_YOGI_SCROLL, 1);
            show("scripts/events/MasterOfEnchanting/32599-scroll-ok.htm", player);
        } else {
            show("scripts/events/MasterOfEnchanting/32599-s1-no.htm", player);
        }
    }

    public void buy_scroll_10() {
        if (player.haveAdena(TEN_SCROLLS_PRICE)) {
            player.reduceAdena(TEN_SCROLLS_PRICE, "MasterOfEnchanting");
            addItem(player, MASTER_YOGI_SCROLL, 10);
            show("scripts/events/MasterOfEnchanting/32599-scroll-ok.htm", player);
        } else {
            show("scripts/events/MasterOfEnchanting/32599-s10-no.htm", player);
        }
    }

    public void receive_reward() {
        int Equip_Id = player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND);
        if (Equip_Id != MASTER_YOGI_STAFF) {
            show("scripts/events/MasterOfEnchanting/32599-rewardnostaff.htm", player);
            return;
        }
        ItemInstance enchanteditem = player.inventory.getItemByItemId(Equip_Id);
        int Ench_Lvl = enchanteditem.getEnchantLevel();

        if (Ench_Lvl > 3) {
            switch (Ench_Lvl) {
                case 4:
                    addItem(player, 6406, 2); // Firework
                    break;
                case 5:
                    addItem(player, 6407, 3); // Firework
                    break;
                case 6:
                    addItem(player, 8752, 1); // HG LS 76
                    break;
                case 7:
                    addItem(player, 8762, 1); // TOP LS 76
                    break;
                case 8:
                    addItem(player, 960, 1); // Scroll: Enchant Weapon (D)
                    break;
                case 9:
                    addItem(player, 959, 1); // Scroll: Enchant Weapon (D)
                    break;
                case 10:
                    addItem(player, 6622, 1); // Scroll: Enchant Weapon (C)
                    break;
                case 11:
                    addItem(player, 9627, 1); // Scroll: Enchant Weapon (C)
                    break;
                case 12:
                    addItem(player, 20335, 1); // EXP Rune (30%)
                    break;
                case 13:
                    addItem(player, 10511, 1); // Shirt A CP
                    break;
                case 14:
                    addItem(player, 10514, 1); // Shirt S CP
                    break;
                case 15:
                    addItem(player, 13953, 1); // Belt (S)
                    break;
                case 16:
                    addItem(player, 13989, 1); // Dyn Armor Box
                    break;
                case 17:
                    addItem(player, 13988, 1); // Dyn Wep Box
                    break;
                case 18:
                    addItem(player, 21587, 1); // 7th Anniv Cloak
                    break;
                default:
                    addItem(player, 21587, 1); // S80 Grade Weapon Chest (Event)
                    break;
            }
            removeItem(player, Equip_Id, 1, "MasterOfEnchanting");
            show("scripts/events/MasterOfEnchanting/32599-rewardok.htm", player);
        } else {
            show("scripts/events/MasterOfEnchanting/32599-rewardnostaff.htm", player);
        }
    }
}