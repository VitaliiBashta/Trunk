package l2trunk.scripts.events.l2day;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.reward.RewardData;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.events.EventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LettersCollection extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(LettersCollection.class);
    // Переменные, определять
    private static boolean _active;
    static String _name;
    static int[][] letters;
    static String _msgStarted;
    static String _msgEnded;

    // Буквы, статика
    static final int A = 3875;
    static final int C = 3876;
    static final int E = 3877;
    static final int F = 3878;
    static final int G = 3879;
    static final int H = 3880;
    static final int I = 3881;
    static final int L = 3882;
    static final int N = 3883;
    static final int O = 3884;
    static final int R = 3885;
    static final int S = 3886;
    static final int T = 3887;
    static final int II = 3888;
    protected static int Y = 13417;
    protected static int _5 = 13418;

    private static final int EVENT_MANAGER_ID = 31230;

    // Контейнеры, не трогать
    static final Map<String, Integer[][]> _words = new HashMap<>();
    static final Map<String, RewardData[]> _rewards = new HashMap<>();
    private static final List<SimpleSpawner> SPAWNS = new ArrayList<>();

    @Override
    public void onLoad() {
        if (isActive()) {
            CharListenerList.addGlobal(this);
            _active = true;
            spawnEventManagers();
            _log.info("Loaded Event: " + _name + " [state: activated]");
        } else
            _log.info("Loaded Event: " + _name + " [state: deactivated]");
    }

    private static boolean isActive() {
        return isActive(_name);
    }

    private void spawnEventManagers() {
        SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS, SPAWNS);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNS);
    }

    @Override
    public void onReload() {
        unSpawnEventManagers();
    }

    @Override
    public void onShutdown() {
        unSpawnEventManagers();
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (_active && SimpleCheckDrop(cha, killer)) {
            int[] letter = Rnd.get(letters);
            if (Rnd.chance(letter[1] * Config.EVENT_L2DAY_LETTER_CHANCE * ((NpcTemplate) cha.getTemplate()).rateHp))
                ((NpcInstance) cha).dropItem(killer.getPlayer(), letter[0], 1);
        }
    }

    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive(_name, true)) {
            spawnEventManagers();
            System.out.println("Event '" + _name + "' started.");
            Announcements.INSTANCE.announceByCustomMessage(_msgStarted);
        } else
            player.sendMessage("Event '" + _name + "' already started.");

        _active = true;

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive(_name, false)) {
            unSpawnEventManagers();
            System.out.println("Event '" + _name + "' stopped.");
            Announcements.INSTANCE.announceByCustomMessage(_msgEnded);
        } else
            player.sendMessage("Event '" + _name + "' not started.");

        _active = false;

        show("admin/events/events.htm", player);
    }

    public void exchange(String[] var) {
        Player player = getSelf();

        if (!player.isQuestContinuationPossible(true))
            return;

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        Integer[][] mss = _words.get(var[0]);

        for (Integer[] l : mss)
            if (getItemCount(player, l[0]) < l[1]) {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                return;
            }

        for (Integer[] l : mss)
            removeItem(player, l[0], l[1], "LettersCollection");

        RewardData[] rewards = _rewards.get(var[0]);
        int sum = 0;
        for (RewardData r : rewards)
            sum += r.getChance();
        int random = Rnd.get(sum);
        sum = 0;
        for (RewardData r : rewards) {
            sum += r.getChance();
            if (sum > random) {
                addItem(player, r.getItemId(), Rnd.get(r.getMinDrop(), r.getMaxDrop()), "LettersCollection");
                return;
            }
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, _msgStarted);
    }

    public String DialogAppend_31230(Integer val) {
        if (!_active)
            return "";

        StringBuilder append = new StringBuilder("<br><br>");
        for (String word : _words.keySet())
            append.append("[scripts_").append(getClass().getName()).append(":exchange ").append(word).append("|").append(word).append("]<br1>");

        return append.toString();
    }
}