package l2trunk.scripts.events.heart;

import l2trunk.commons.text.PrintfFormat;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Heart extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger LOG = LoggerFactory.getLogger(Heart.class);
    private static final List<SimpleSpawner> SPAWNS = new ArrayList<>();
    private static final Map<Integer, Integer> Guesses = new HashMap<>();
    private static final String[][] variants = {{"Rock", "Камень"}, {"Scissors", "Ножницы"}, {"Paper", "Бумага"}};
    private static final int EVENT_MANAGER_ID = 31227; //Buzz the Cat
    private static final List<Integer> hearts = List.of(4209, 4210, 4211, 4212, 4213, 4214, 4215, 4216, 4217);
    private static final List<Integer> potions = List.of(1374, // Greater Haste Potion
            1375, // Greater Swift Attack Potion
            6036, // Greater Magic Haste Potion
            1539 // Greater Healing Potion
    );
    private static final List<Integer> scrolls = List.of(3926, //	L2Day - Scroll of Guidance
            3927, //	L2Day - Scroll of Death Whisper
            3928, //	L2Day - Scroll of Focus
            3929, //	L2Day - Scroll of Greater Acumen
            3930, //	L2Day - Scroll of Haste
            3931, //	L2Day - Scroll of Agility
            3932, //	L2Day - Scroll of Mystic Empower
            3933, //	L2Day - Scroll of Might
            3934, //	L2Day - Scroll of Windwalk
            3935 //	L2Day - Scroll of Shield
    );
    private static boolean active = false;
    private static String links_en = "";
    private static String links_ru = "";

    static {
        PrintfFormat fmt = new PrintfFormat("<br><a action=\"bypass -h scripts_events.Heart.Heart:play %d\">\"%s!\"</a>");
        for (int i = 0; i < variants.length; i++) {
            links_en += fmt.sprintf(i, variants[i][0]);
            links_ru += fmt.sprintf(i, variants[i][1]);
        }
    }

    private static boolean isActive() {
        return isActive("Heart");
    }

    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("Heart", true)) {
            spawnEventManagers();
            System.out.println("Event 'Change of Heart' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Change of Heart' already started.");

        active = true;
        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive("Heart", false)) {
            unSpawnEventManagers();
            System.out.println("Event 'Change of Heart' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'Change of Heart' not started.");

        active = false;

        show("admin/events/events.htm", player);
    }

    public void letsplay() {
        Player player = getSelf();

        if (!player.isQuestContinuationPossible(true))
            return;

        zeroGuesses(player);
        if (haveAllHearts(player))
            show(link(HtmCache.INSTANCE.getNotNull("scripts/events/Heart/hearts_01.htm", player), isRus(player)), player);
        else
            show("scripts/events/Heart/hearts_00.htm", player);
    }

    public void play(String[] var) {
        Player player = getSelf();

        if (!player.isQuestContinuationPossible(true) || var.length == 0)
            return;

        if (!haveAllHearts(player)) {
            if (var[0].equalsIgnoreCase("Quit"))
                show("scripts/events/Heart/hearts_00b.htm", player);
            else
                show("scripts/events/Heart/hearts_00a.htm", player);
            return;
        }

        if (var[0].equalsIgnoreCase("Quit")) {
            int curr_guesses = getGuesses(player);
            takeHeartsSet(player);
            reward(player, curr_guesses);
            show("scripts/events/Heart/hearts_reward_" + curr_guesses + ".htm", player);
            zeroGuesses(player);
            return;
        }

        int var_cat = Rnd.get(variants.length);
        int var_player = 0;
        try {
            var_player = Integer.parseInt(var[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (var_player == var_cat) {
            show(fillvars(HtmCache.INSTANCE.getNotNull("scripts/events/Heart/hearts_same.htm", player), var_player, var_cat, player), player);
            return;
        }

        if (playerWins(var_player, var_cat)) {
            incGuesses(player);
            int curr_guesses = getGuesses(player);
            if (curr_guesses == 10) {
                takeHeartsSet(player);
                reward(player, curr_guesses);
                zeroGuesses(player);
            }
            show(fillvars(HtmCache.INSTANCE.getNotNull("scripts/events/Heart/hearts_level_" + curr_guesses + ".htm", player), var_player, var_cat, player), player);
            return;
        }

        takeHeartsSet(player);
        reward(player, getGuesses(player) - 1);
        show(fillvars(HtmCache.INSTANCE().getNotNull("scripts/events/Heart/hearts_loose.htm", player), var_player, var_cat, player), player);
        zeroGuesses(player);
    }

    private void reward(Player player, int guesses) {
        switch (guesses) {
            case -1:
            case 0:
                addItem(player, Rnd.get(scrolls), 1, "heardReward");
                break;
            case 1:
                addItem(player, Rnd.get(potions), 10, "heardReward");
                break;
            case 2:
                addItem(player, 1538, 1, "heardReward"); // 1  Blessed Scroll of Escape
                break;
            case 3:
                addItem(player, 3936, 1, "heardReward"); // 1  Blessed Scroll of Resurrection
                break;
            case 4:
                addItem(player, 951, 2, "heardReward"); // 2  Scroll: Enchant Weapon (C)
                break;
            case 5:
                addItem(player, 948, 4, "heardReward"); // 4  Scroll: Enchant Armor (B)
                break;
            case 6:
                addItem(player, 947, 1, "heardReward"); // 1  Scroll: Enchant Weapon (B)
                break;
            case 7:
                addItem(player, 730, 3, "heardReward"); // 3  Scroll: Enchant Armor (A)
                break;
            case 8:
                addItem(player, 729, 1, "heardReward"); // 1  Scroll: Enchant Weapon (A)
                break;
            case 9:
                addItem(player, 960, 2, "heardReward"); // 2  Scroll: Enchant Armor (S)
                break;
            case 10:
                addItem(player, 959, 1, "heardReward"); // 1  Scroll: Enchant Weapon (S)
                break;
        }
    }

    private String fillvars(String s, int var_player, int var_cat, Player player) {
        boolean rus = isRus(player);
        return link(s.replaceFirst("Player", player.getName()).replaceFirst("%var_payer%", variants[var_player][rus ? 1 : 0]).replaceFirst("%var_cat%", variants[var_cat][rus ? 1 : 0]), rus);
    }

    private boolean isRus(Player player) {
        return player.isLangRus();
    }

    private String link(String s, boolean rus) {
        return s.replaceFirst("%links%", rus ? links_ru : links_en);
    }

    private boolean playerWins(int var_player, int var_cat) {
        if (var_player == 0) // Rock vs Scissors
            return var_cat == 1;
        if (var_player == 1) // Scissors vs Paper
            return var_cat == 2;
        if (var_player == 2) // Paper vs Rock
            return var_cat == 0;
        return false;
    }

    private int getGuesses(Player player) {
        return Guesses.getOrDefault(player.getObjectId(), 0);
    }

    private void incGuesses(Player player) {
        int val = 1;
        if (Guesses.containsKey(player.getObjectId()))
            val = Guesses.remove(player.getObjectId()) + 1;
        Guesses.put(player.getObjectId(), val);
    }

    private void zeroGuesses(Player player) {
        Guesses.remove(player.getObjectId());
    }

    private void takeHeartsSet(Player player) {
        for (int heart_id : hearts)
            removeItem(player, heart_id, 1, "heardReward");
    }

    private boolean haveAllHearts(Player player) {
        return hearts.stream()
                .noneMatch(heart_id -> (player.getInventory().getCountOf(heart_id) < 1));
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (active && SimpleCheckDrop(cha, killer))
            ((NpcInstance) cha).dropItem(killer.getPlayer(), Rnd.get(hearts), Util.rollDrop(1, 1, Config.EVENT_CHANGE_OF_HEART_CHANCE * killer.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp * 10000L, true));
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.ChangeofHeart.AnnounceEventStarted");
    }

    private void spawnEventManagers() {
        final List<Location> EVENT_MANAGERS = List.of(
                new Location(146936, 26654, -2208, 16384), // Aden
                new Location(82168, 148842, -3464, 7806), // Giran
                new Location(82204, 53259, -1488, 16384), // Oren
                new Location(18924, 145782, -3088, 44034), // Dion
                new Location(111794, 218967, -3536, 20780), // Heine
                new Location(-14539, 124066, -3112, 50874), // Gludio
                new Location(147271, -55573, -2736, 60304), // Goddard
                new Location(87801, -143150, -1296, 28800), // Shuttgard
                new Location(-80684, 149458, -3040, 16384)); // Gludin

        SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS, SPAWNS);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNS);
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            active = true;
            spawnEventManagers();
            LOG.info("Loaded Event: Change of Heart [state: activated]");
        } else
            LOG.info("Loaded Event: Change of Heart[state: deactivated]");
    }

    @Override
    public void onReload() {
        unSpawnEventManagers();
    }

    @Override
    public void onShutdown() {
        unSpawnEventManagers();
    }
}