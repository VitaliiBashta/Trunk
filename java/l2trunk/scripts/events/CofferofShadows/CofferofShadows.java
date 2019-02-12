package l2trunk.scripts.events.CofferofShadows;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
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

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.gameserver.utils.ItemFunctions.addItem;

// Эвент Coffer of Shadows
public class CofferofShadows extends Functions implements ScriptFile, OnPlayerEnterListener {
    private static final int COFFER_PRICE = 50000; // 50.000 adena at x1 servers
    private static final int COFFER_ID = 8659;
    private static final int EVENT_MANAGER_ID = 32091;
    private static  List<SimpleSpawner> _spawns = new ArrayList<>();
    private static final Logger _log = LoggerFactory.getLogger(CofferofShadows.class);
    private static boolean _active = false;

    private void spawnEventManagers() {
        _spawns = SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS_coffer_march8);
    }

    /**
     * Удаляет спавн эвент менеджеров
     */
    private void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
    }

    private static boolean isActive() {
        return isActive("CofferofShadows");
    }

    /**
     * Запускает эвент
     */
    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive("CofferofShadows", true)) {
            spawnEventManagers();
            System.out.println("Event: Coffer of Shadows started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Coffer of Shadows' already started.");

        _active = true;
        show("admin/events/events.htm", player);
    }

    /**
     * Останавливает эвент
     */
    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive("CofferofShadows", false)) {
            unSpawnEventManagers();
            System.out.println("Event: Coffer of Shadows stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.CofferofShadows.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'Coffer of Shadows' not started.");

        _active = false;
        show("admin/events/events.htm", player);
    }

    /**
     * Продает 1 сундук игроку
     *
     * @param var
     */
    public void buycoffer(String[] var) {

        if (!player.isQuestContinuationPossible(true))
            return;

        if (!NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        int coffer_count = toInt(var[0],1);

        long need_adena = (long) (COFFER_PRICE * Config.EVENT_CofferOfShadowsPriceRate * coffer_count);
        if (player.haveAdena( need_adena)) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        player.reduceAdena(need_adena, true, "BuyCofferOfShadows");
        addItem(player, COFFER_ID, coffer_count);
    }

    /**
     * Добавляет в диалоги эвент менеджеров строчку с байпасом для покупки сундука
     */
    private static final int[] buycoffer_counts = {1, 5, 10, 50}; //TODO в конфиг

    public String DialogAppend_32091(Integer val) {
        if (val != 0)
            return "";

        String price;
        String append = "";
        for (int cnt : buycoffer_counts) {
            price = Util.formatAdena((long) (COFFER_PRICE * Config.EVENT_CofferOfShadowsPriceRate * cnt));
            append += "<a action=\"bypass -h scripts_events.CofferofShadows.CofferofShadows:buycoffer " + cnt + "\">";
            if (cnt == 1)
                append += new CustomMessage("scripts.events.CofferofShadows.buycoffer", player).addString(price);
            else
                append += new CustomMessage("scripts.events.CofferofShadows.buycoffers", player).addNumber(cnt).addString(price);
            append += "</a><br>";
        }

        return append;
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            spawnEventManagers();
            _log.info("Loaded Event: Coffer of Shadows [state: activated]");
        } else
            _log.info("Loaded Event: Coffer of Shadows [state: deactivated]");
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
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.CofferofShadows.AnnounceEventStarted");
    }
}