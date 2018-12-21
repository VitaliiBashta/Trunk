package l2trunk.scripts.events.SummerMeleons;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.events.EventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class SummerMeleons extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(SummerMeleons.class);
    private static final int EVENT_MANAGER_ID = 32636;
    private static final List<SimpleSpawner> SPAWNS = new ArrayList<>();

    private static boolean active = false;
    private static boolean MultiSellLoaded = false;

    private final Path multiSellFile = Config.DATAPACK_ROOT.resolve("data/multisell/events/SummerMeleons/3790004.xml");

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            active = true;
            loadMultiSell();
            spawnEventManagers();
            _log.info("Loaded Event: Summer Meleons [state: activated]");
        } else
            _log.info("Loaded Event: Summer Meleons [state: deactivated]");
    }

    private static boolean isActive() {
        return isActive("SummerMeleons");
    }

    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("SummerMeleons", true)) {
            loadMultiSell();
            spawnEventManagers();
            System.out.println("Event 'Summer Meleons' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.SummerMeleons.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Summer Meleons' already started.");

        active = true;

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive("SummerMeleons", false)) {
            unSpawnEventManagers();
            System.out.println("Event 'Summer Meleons' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.SummerMeleons.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'Summer Meleons' not started.");

        active = false;

        show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS_harvest_meleons, SPAWNS);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNS);
    }

    private void loadMultiSell() {
        if (MultiSellLoaded)
            return;
        MultiSellHolder.INSTANCE.parseFile(multiSellFile);
        MultiSellLoaded = true;
    }

    @Override
    public void onReload() {
        unSpawnEventManagers();
        if (MultiSellLoaded) {
            MultiSellHolder.INSTANCE.remove(multiSellFile);
            MultiSellLoaded = false;
        }
    }

    @Override
    public void onShutdown() {
    }


    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (active && SimpleCheckDrop(cha, killer) && Rnd.chance(Config.EVENT_TFH_POLLEN_CHANCE * killer.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
            ((NpcInstance) cha).dropItem(killer.getPlayer(), 6391, 1);
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.SummerMeleons.AnnounceEventStarted");
    }
}