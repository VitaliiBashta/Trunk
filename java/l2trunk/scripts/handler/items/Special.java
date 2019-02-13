package l2trunk.scripts.handler.items;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;
import l2trunk.scripts.bosses.AntharasManager;
import l2trunk.scripts.bosses.ValakasManager;
import l2trunk.scripts.quests._464_Oath;

import java.util.Calendar;
import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Special extends SimpleItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(
            8060, 8556, 13853, 13808, 13809, 20630, 21106, 21107, 14835, 15537,
            10632, 21899, 21900, 21901, 21902, 21903, 21904, 17268);

    private static void useItem(Player player, int itemId, long count) {
        player.sendPacket(new SystemMessage(SystemMessage.YOU_USE_S1).addItemName(itemId));
        removeItem(player, itemId, count, "useItem");
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        switch (itemId) {
            //Coin - PrimeShop Points adder
            case 4356:
                return use4356(player, ctrl);
            //Key of Enigma
            case 8060:
                return use8060(player, ctrl);
            //Dewdrop of Destruction
            case 8556:
                return use8556(player, ctrl);
            // My teleport Spellbook
            case 13015:
                return use13015(player, ctrl);
            //DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowder
            case 13853:
                return use13853(player, ctrl);
            //Holy Water for SSQ 2nd quest
            case 13808:
                return use13808(player, ctrl);
            //Court Mag Staff for SSQ 2nd quest
            case 13809:
                return use13809(player, ctrl);
            case 14835:
                return use14835(player, ctrl);
            //Strongbox of Promise
            case 15537:
                return use15537(player, ctrl);
            //Wondrous Cubic
            case 10632:
                return use10632(player, ctrl);
            //refined wondrous cube
            case 21107:
                return use21107(player, ctrl);
            //Wondrous Cubic 1 time use
            case 21106:
                return use21106(player, ctrl);
            //Soul Magic Box
            case 20630:
                return use20630(player, ctrl);
            case 21899:
                return use21899(player, ctrl);
            case 21900:
                return use21900(player, ctrl);
            case 21901:
                return use21901(player, ctrl);
            case 21902:
                return use21902(player, ctrl);
            case 21903:
                return use21903(player, ctrl);
            case 21904:
                return use21904(player, ctrl);
            //Antharas Blood Crystal
            case 17268:
                return use17268(player, ctrl);
            default:
                return false;
        }
    }

    //Coin - PrimeShop Points adder
    private boolean use4356(Player player, boolean ctrl) {
        if (player == null) {
            return false;
        }
        int rnd = Rnd.get(1, 5);
        player.sendMessage("Not Working");
        return true;
    }

    // Wondrous Cubic 1 time use
    private boolean use21106(Player player, boolean ctrl) {
        removeItem(player, 21106, 1, "use21106");

        int chance = Rnd.get(100);

        if (chance >= 0 && chance < 2) addItem(player, 21593, 1);

        else if (chance >= 2 && chance < 20)
            addItem(player, 10633, 1);

        else if (chance >= 20 && chance < 37)
            addItem(player, 10642, 1);

        else if (chance >= 37 && chance < 43)
            addItem(player, 21096, 1);

        else if (chance >= 43 && chance < 49)
            addItem(player, 21101, 1);

        else if (chance >= 49 && chance < 55)
            addItem(player, 10634, 1);

        else if (chance >= 55 && chance < 60)
            addItem(player, 10643, 1);

        else if (chance >= 60 && chance < 65)
            addItem(player, 21097, 1);

        else if (chance >= 65 && chance < 70)
            addItem(player, 21102, 1);

        else if (chance >= 70 && chance < 74)
            addItem(player, 10635, 1);

        else if (chance >= 74 && chance < 78)
            addItem(player, 10644, 1);

        else if (chance >= 78 && chance < 82)
            addItem(player, 21098, 1);

        else if (chance >= 82 && chance < 86)
            addItem(player, 21103, 1);

        else if (chance >= 86 && chance < 89)
            addItem(player, 10636, 1);

        else if (chance >= 89 && chance < 92)
            addItem(player, 10645, 1);

        else if (chance >= 92 && chance < 94)
            addItem(player, 21099, 1);

        else if (chance >= 94 && chance < 96)
            addItem(player, 21104, 1);

        else if (chance >= 96 && chance < 97)
            addItem(player, 10637, 1);

        else if (chance >= 97 && chance < 98)
            addItem(player, 10646, 1);

        else if (chance >= 98 && chance < 99)
            addItem(player, 21100, 1);

        else if (chance >= 99)
            addItem(player, 21105, 1);

        return true;
    }

    private boolean use21107(Player player, boolean ctrl) {
        removeItem(player, 21107, 1, "use21107");

        int chance = Rnd.get(100);

        if (chance >= 0 && chance < 6)
            addItem(player, 12845, 1);

        else if (chance >= 6 && chance < 12)
            addItem(player, 12846, 1);

        else if (chance >= 12 && chance < 18)
            addItem(player, 12847, 1);

        else if (chance >= 18 && chance < 24)
            addItem(player, 12848, 1);

        else if (chance >= 24 && chance < 29)
            addItem(player, 12849, 1);

        else if (chance >= 29 && chance < 34)
            addItem(player, 12850, 1);

        else if (chance >= 34 && chance < 39)
            addItem(player, 12851, 1);

        else if (chance >= 39 && chance < 44)
            addItem(player, 14166, 1);

        else if (chance >= 44 && chance < 48)
            addItem(player, 8738, 1);

        else if (chance >= 48 && chance < 52)
            addItem(player, 8739, 1);

        else if (chance >= 52 && chance < 56)
            addItem(player, 8740, 1);

        else if (chance >= 56 && chance < 60)
            addItem(player, 8741, 1);

        else if (chance >= 60 && chance < 64)
            addItem(player, 8742, 1);

        else if (chance >= 64 && chance < 68)
            addItem(player, 9574, 1);

        else if (chance >= 68 && chance < 72)
            addItem(player, 10484, 1);

        else if (chance >= 72 && chance < 76)
            addItem(player, 14167, 1);

        else if (chance >= 76 && chance < 78)
            addItem(player, 8748, 1);

        else if (chance >= 78 && chance < 80)
            addItem(player, 8749, 1);

        else if (chance >= 80 && chance < 82)
            addItem(player, 8750, 1);

        else if (chance >= 82 && chance < 84)
            addItem(player, 8751, 1);

        else if (chance >= 84 && chance < 86)
            addItem(player, 8752, 1);

        else if (chance >= 86 && chance < 88)
            addItem(player, 9575, 1);

        else if (chance >= 88 && chance < 90)
            addItem(player, 10485, 1);

        else if (chance >= 90 && chance < 92)
            addItem(player, 14168, 1);

        else if (chance >= 92 && chance < 93)
            addItem(player, 8958, 1);

        else if (chance >= 93 && chance < 94)
            addItem(player, 8959, 1);

        else if (chance >= 94 && chance < 95)
            addItem(player, 8960, 1);

        else if (chance >= 95 && chance < 96)
            addItem(player, 8961, 1);

        else if (chance >= 96 && chance < 97)
            addItem(player, 8962, 1);

        else if (chance >= 97 && chance < 98)
            addItem(player, 9576, 1);

        else if (chance >= 98 && chance < 99)
            addItem(player, 10486, 1);

        else if (chance >= 99)
            addItem(player, 14169, 1);

        return true;
    }

    //Key of Enigma
    private boolean use8060(Player player, boolean ctrl) {
        if (removeItem(player, 8058, 1, "use8060") == 1) {
            addItem(player, 8059, 1);
            return true;
        }
        return false;
    }

    private boolean use20630(Player player, boolean ctrl) {
        addItem(player, 20602, 1);
        addItem(player, 20603, 1);
        return true;
    }

    //Dewdrop of Destruction
    private boolean use8556(Player player, boolean ctrl) {
        List<Integer> npcs = List.of(29048, 29049);

        GameObject t = player.getTarget();
        if (!(t instanceof NpcInstance) || !npcs.contains(((NpcInstance) t).getNpcId())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(8556));
            return false;
        }
        if (player.getDistance(t) > 200) {
            player.sendPacket(new SystemMessage(SystemMessage.YOUR_TARGET_IS_OUT_OF_RANGE));
            return false;
        }

        useItem(player, 8556, 1);
        ((NpcInstance) t).doDie(player);
        return true;
    }

    //DestroyedDarknessFragmentPowder -> DestroyedLightFragmentPowde
    private boolean use13853(Player player, boolean ctrl) {
        if (!player.isInZone(ZoneType.mother_tree)) {
            player.sendPacket(Msg.THERE_WAS_NOTHING_FOUND_INSIDE_OF_THAT);
            return false;
        }
        useItem(player, 13853, 1);
        addItem(player, 13854, 1);
        return true;
    }

    //Holy Water for SSQ 2nd quest
    private boolean use13808(Player player, boolean ctrl) {
        List<Integer> allowedDoors = List.of(17240101, 17240105, 17240109);

        GameObject target = player.getTarget();
        if (player.getDistance(target) > 150)
            return false;
        if (target instanceof DoorInstance) {
            int _door = ((DoorInstance) target).getDoorId();
            if (allowedDoors.contains(_door))
                player.getReflection().openDoor(_door);
            else {
                player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
        } else {
            player.sendPacket(Msg.INVALID_TARGET);
            return false;
        }
        return true;
    }

    // My teleport SpellBook (Capacity added + 3)
    private boolean use13015(Player player, boolean ctrl) {
        if (player.bookmarks.getCapacity() >= 30) {
            player.sendPacket(new SystemMessage(SystemMessage.YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT));
            return false;
        }
        player.bookmarks.setCapacity(player.bookmarks.getCapacity() + 3);
        player.sendPacket(new SystemMessage(SystemMessage.THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED));
        removeItem(player, 13015, 1, "use13015");
        return true;
    }

    //Court Mag Staff for SSQ 2nd quest
    private boolean use13809(Player player, boolean ctrl) {
        List<Integer> allowedDoors = List.of(17240103, 17240107);

        GameObject target = player.getTarget();
        if (target instanceof DoorInstance) {
            int _door = ((DoorInstance) target).getDoorId();
            if (allowedDoors.contains(_door)) {
                useItem(player, 13809, 1);
                player.getReflection().openDoor(_door);
                return false;
            } else {
                player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }
        } else {
            player.sendPacket(Msg.INVALID_TARGET);
            return false;
        }
    }

    private boolean use14835(Player player, boolean ctrl) {
        //TODO [G1ta0] добавить кучу других проверок на возможность телепорта
        if (player.isActionsDisabled() || player.isInOlympiadMode() || player.isInZone(ZoneType.no_escape)) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(14835));
            return false;
        }

        useItem(player, 14835, 1);
        //Stakato nest entrance
        player.teleToLocation(new Location(89464, -44712, -2167), ReflectionManager.DEFAULT);
        return true;
    }

    //Strongbox of Promise
    private boolean use15537(Player player, boolean ctrl) {
        QuestState qs = player.getQuestState(_464_Oath.class);
        if (player.getLevel() >= 82 && qs == null) {
            useItem(player, 15537, 1);
            addItem(player, 15538, 1);
            Quest q = QuestManager.getQuest(_464_Oath.class);
            QuestState st = q.newQuestState(player, Quest.CREATED);
            st.setState(Quest.STARTED);
            st.setCond(1);
        } else {
            player.sendMessage(new CustomMessage("Quest._464_Oath.QuestCannotBeTaken"));
            return false;
        }
        return true;
    }

    //Wondrous Cubic
    private boolean use10632(Player player, boolean ctrl) {
        Calendar currentTime = Calendar.getInstance();
        if (currentTime.get(Calendar.HOUR_OF_DAY) == 6 && currentTime.get(Calendar.MINUTE) == 30) {
            player.sendMessage("Not available at the moment!");
            return false;
        }

        int chance = Rnd.get(1000000);

        if (chance < 350000) // Rough Blue Cubic Piece            35%
            addItem(player, 10633, 1);
        else if (chance < 550000) // Rough Yellow Cubic Piece     20%
            addItem(player, 10634, 1);
        else if (chance < 650000) // Rough Green Cubic Piece      10%
            addItem(player, 10635, 1);
        else if (chance < 730000) // Rough Red Cubic Piece        8%
            addItem(player, 10636, 1);
        else if (chance < 750000) // Rough White Cubic Piece      2%
            addItem(player, 10637, 1);

        else if (chance < 890000) // Fine Blue Cubic Piece        14%
            addItem(player, 10642, 1);
        else if (chance < 960000) // Fine Yellow Cubic Piece      7%
            addItem(player, 10643, 1);
        else if (chance < 985000) // Fine Green Cubic Piece       2.5%
            addItem(player, 10644, 1);
        else if (chance < 995000) // Fine Red Cubic Piece         1%
            addItem(player, 10645, 1);
        else if (chance <= 1000000) // Fine White Cubic Piece     0.5%
            addItem(player, 10646, 1);

        return true;
    }

    //Totem
    private boolean use21899(Player player, boolean ctrl) {
        if (!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21899));
            return false;
        }
        NpcUtils.spawnSingle(143, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
        return true;
    }

    //Totem
    private boolean use21900(Player player, boolean ctrl) {
        if (!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21900));
            return false;
        }
        NpcUtils.spawnSingle(144, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
        return true;
    }

    //Totem
    private boolean use21901(Player player, boolean ctrl) {
        if (!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21901));
            return false;
        }
        NpcUtils.spawnSingle(145, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
        return true;
    }

    //Totem
    private boolean use21902(Player player, boolean ctrl) {
        if (!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21902));
            return false;
        }
        NpcUtils.spawnSingle(146, Location.findPointToStay(player.getLoc(), 50, 100, player.getGeoIndex()));
        return true;
    }

    // Refined Red Dragon Blood
    private boolean use21903(Player player, boolean ctrl) {
        if (!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21903));
            return false;
        }
        player.doCast(22298, player, false);
        removeItem(player, 21903, 1, "use21903");
        return true;
    }

    // Refined Blue Dragon Blood
    private boolean use21904(Player player, boolean ctrl) {
        if (!player.isInZone(AntharasManager.getZone()) && !player.isInZone(ValakasManager.getZone())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(21904));
            return false;
        }
        player.doCast(22299, player, false);
        removeItem(player, 21904, 1, "use21904");
        return true;
    }

    // Antharas Blood Crystal
    private boolean use17268(Player player, boolean ctrl) {
        if (!player.isInZone(AntharasManager.getZone())) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(17268));
            return false;
        }
        player.doCast(9179, player, false);
        removeItem(player, 17268, 1, "use17268");
        return true;
    }
}
