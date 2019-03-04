package l2trunk.scripts.events.heart;

import l2trunk.commons.text.PrintfFormat;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Heart extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger LOG = LoggerFactory.getLogger(Heart.class);
    private static List<SimpleSpawner> SPAWNS;
    private static final Map<Integer, Integer> GUESSES = new HashMap<>();
    private static final List<String> variants = List.of("Rock", "Scissors", "Paper");
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

    static {
        PrintfFormat fmt = new PrintfFormat("<br><a action=\"bypass -h scripts_events.Heart.Heart:play %d\">\"%s!\"</a>");
        for (int i = 0; i < variants.size(); i++) {
            links_en += fmt.sprintf(i, variants.get(i));
        }
    }

    private static boolean isActive() {
        return isActive("Heart");
    }

    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive("Heart", true)) {
            spawnEventManagers();
            System.out.println("Event 'Change of Heart' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Change of Heart' already started.");

        active = true;
        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive("Heart", false)) {
            unSpawnEventManagers();
            System.out.println("Event 'Change of Heart' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.ChangeofHeart.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'Change of Heart' not started.");

        active = false;

        show("admin/events/events.htm", player);
    }

    public void letsplay() {
        if (!player.isQuestContinuationPossible(true))
            return;

        zeroGuesses(player);
        if (haveAllHearts(player))
            show(link(HtmCache.INSTANCE.getNotNull("scripts/events/Heart/hearts_01.htm", player)), player);
        else
            show("scripts/events/Heart/hearts_00.htm", player);
    }

    public void play(String[] var) {
        if (!player.isQuestContinuationPossible(true) || var.length == 0)
            return;

        if (!haveAllHearts(player)) {
            if ("Quit".equalsIgnoreCase(var[0]))
                show("scripts/events/Heart/hearts_00b.htm", player);
            else
                show("scripts/events/Heart/hearts_00a.htm", player);
            return;
        }

        if ("Quit".equalsIgnoreCase(var[0])) {
            int curr_guesses = getGuesses(player);
            takeHeartsSet(player);
            reward(player, curr_guesses);
            show("scripts/events/Heart/hearts_reward_" + curr_guesses + ".htm", player);
            zeroGuesses(player);
            return;
        }

        int var_cat = Rnd.get(variants.size());
        int var_player;
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
        show(fillvars(HtmCache.INSTANCE.getNotNull("scripts/events/Heart/hearts_loose.htm", player), var_player, var_cat, player), player);
        zeroGuesses(player);
    }

    private void reward(Player player, int guesses) {
        switch (guesses) {
            case -1:
            case 0:
                addItem(player, Rnd.get(scrolls), 1);
                break;
            case 1:
                addItem(player, Rnd.get(potions), 10);
                break;
            case 2:
                addItem(player, 1538); // 1  Blessed Scroll of Escape
                break;
            case 3:
                addItem(player, 3936); // 1  Blessed Scroll of Resurrection
                break;
            case 4:
                addItem(player, 951, 2); // 2  Scroll: Enchant Weapon (C)
                break;
            case 5:
                addItem(player, 948, 4); // 4  Scroll: Enchant Armor (B)
                break;
            case 6:
                addItem(player, 947, 1); // 1  Scroll: Enchant Weapon (B)
                break;
            case 7:
                addItem(player, 730, 3); // 3  Scroll: Enchant Armor (A)
                break;
            case 8:
                addItem(player, 729); // 1  Scroll: Enchant Weapon (A)
                break;
            case 9:
                addItem(player, 960, 2); // 2  Scroll: Enchant Armor (S)
                break;
            case 10:
                addItem(player, 959, 1); // 1  Scroll: Enchant Weapon (S)
                break;
        }
    }

    private String fillvars(String s, int var_player, int var_cat, Player player) {
        return link(s.replaceFirst("Player", player.getName()).replaceFirst("%var_payer%", variants.get(var_player)).replaceFirst("%var_cat%", variants.get(var_cat)));
    }

    private String link(String s) {
        return s.replaceFirst("%links%", links_en);
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
        return GUESSES.getOrDefault(player.objectId(), 0);
    }

    private void incGuesses(Player player) {
        int val = 1;
        if (GUESSES.containsKey(player.objectId()))
            val = GUESSES.remove(player.objectId()) + 1;
        GUESSES.put(player.objectId(), val);
    }

    private void zeroGuesses(Player player) {
        GUESSES.remove(player.objectId());
    }

    private void takeHeartsSet(Player player) {
        for (int heart_id : hearts)
            removeItem(player, heart_id, 1, "heardReward");
    }

    private boolean haveAllHearts(Player player) {
        return hearts.stream().allMatch(player::haveItem);
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (killer instanceof Playable) {
            Playable playable = (Playable) killer;
            if (active && simpleCheckDrop(cha, playable))
                ((NpcInstance) cha).dropItem(playable.getPlayer(), Rnd.get(hearts), Util.rollDrop(1, 1, Config.EVENT_CHANGE_OF_HEART_CHANCE * playable.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp * 10000L, true));
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.ChangeofHeart.AnnounceEventStarted");
    }

    private void spawnEventManagers() {
        final List<Location> EVENT_MANAGERS = List.of(
                Location.of(146936, 26654, -2208, 16384), // Aden
                Location.of(82168, 148842, -3464, 7806), // Giran
                Location.of(82204, 53259, -1488, 16384), // Oren
                Location.of(18924, 145782, -3088, 44034), // Dion
                Location.of(111794, 218967, -3536, 20780), // Heine
                Location.of(-14539, 124066, -3112, 50874), // Gludio
                Location.of(147271, -55573, -2736, 60304), // Goddard
                Location.of(87801, -143150, -1296, 28800), // Shuttgard
                Location.of(-80684, 149458, -3040, 16384)); // Gludin

        SPAWNS = SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS);
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