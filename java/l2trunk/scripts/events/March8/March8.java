package l2trunk.scripts.events.March8;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Util;
import l2trunk.scripts.events.EventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class March8 extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(March8.class);
    private static final String EVENT_NAME = "March8";
    private static final int RECIPE_PRICE = 50000; // 50.000 adena at x1 servers
    private static final int RECIPE_ID = 20191;
    private static final int EVENT_MANAGER_ID = 4301;
    private static List<SimpleSpawner> SPAWNER_LIST = new ArrayList<>();
    private static final List<Integer> DROP = List.of(20192, 20193, 20194);
    private static boolean _active = false;

    private static boolean isActive() {
        return isActive(EVENT_NAME);
    }

    private void spawnEventManagers() {
        SPAWNER_LIST = SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS_coffer_march8);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNER_LIST);
    }

    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive(EVENT_NAME, true)) {
            spawnEventManagers();
            System.out.println("Event: March 8 started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.March8.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'March 8' already started.");

        _active = true;
        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive(EVENT_NAME, false)) {
            unSpawnEventManagers();
            System.out.println("Event: March 8 stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.March8.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'March 8' not started.");

        _active = false;
        show("admin/events/events.htm", player);
    }

    public void buyrecipe() {
        if (!player.isQuestContinuationPossible(true))
            return;

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        long need_adena = (long) (RECIPE_PRICE * Config.EVENT_MARCH8_PRICE_RATE);
        if (player.getAdena() < need_adena) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        player.reduceAdena(need_adena, true, "March8");
        addItem(player, RECIPE_ID, 1);
    }

    public String DialogAppend_4301(Integer val) {
        if (val != 0)
            return "";

        String price;
        String append = "";
        price = Util.formatAdena((long) (RECIPE_PRICE * Config.EVENT_MARCH8_PRICE_RATE));
        append += "<br><a action=\"bypass -h scripts_events.March8.March8:buyrecipe\">";
        append += new CustomMessage("scripts.events.March8.buyrecipe", player).addString(price);
        append += "</a><br>";
        return append;
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            spawnEventManagers();
            _log.info("Loaded Event: March 8 [state: activated]");
        } else
            _log.info("Loaded Event: March 8 [state: deactivated]");
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
    public void onPlayerEnter(Player player) {
        if (_active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.March8.AnnounceEventStarted");
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (killer instanceof Playable) {
            Playable playable = (Playable) killer;
            if (_active && simpleCheckDrop(cha, playable) && Rnd.chance(Config.EVENT_MARCH8_DROP_CHANCE * playable.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
                ((NpcInstance) cha).dropItem(playable.getPlayer(), Rnd.get(DROP), 1);
        }
    }
}