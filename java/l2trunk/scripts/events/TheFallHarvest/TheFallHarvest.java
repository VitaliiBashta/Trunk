package l2trunk.scripts.events.TheFallHarvest;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.events.EventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class TheFallHarvest extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(TheFallHarvest.class);
    private static final int EVENT_MANAGER_ID = 31255;
    private static List<SimpleSpawner> SPAWNS = new ArrayList<>();

    private static boolean _active = false;
    private static boolean MultiSellLoaded = false;

    private final Path multiSellFile = Config.DATAPACK_ROOT.resolve("data/multisell/events/TheFallHarvest/31255.xml");

    private static boolean isActive() {
        return isActive("TheFallHarvest");
    }

    private void loadMultiSell() {
        if (MultiSellLoaded)
            return;
        MultiSellHolder.INSTANCE.parseFile(multiSellFile);
        MultiSellLoaded = true;
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            loadMultiSell();
            spawnEventManagers();
            _log.info("Loaded Event: The Fall Harvest [state: activated]");
        } else
            _log.info("Loaded Event: The Fall Harvest [state: deactivated]");
    }

    /**
     * Запускает эвент
     */
    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive("TheFallHarvest", true)) {
            loadMultiSell();
            spawnEventManagers();
            System.out.println("Event 'The Fall Harvest' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.TheFallHarvest.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'The Fall Harvest' already started.");

        _active = true;

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive("TheFallHarvest", false)) {
            unSpawnEventManagers();
            System.out.println("Event 'The Fall Harvest' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.TheFallHarvest.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'The Fall Harvest' not started.");

        _active = false;

        show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        SPAWNS = SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS_harvest_meleons);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNS);
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
    public void onDeath(Creature cha, Creature killer) {
        if (killer instanceof Playable) {
            Playable playable = (Playable) killer;
            if (_active && simpleCheckDrop(cha, playable) && Rnd.chance(Config.EVENT_TFH_POLLEN_CHANCE * playable.getPlayer().getRateItems() * ((NpcInstance) cha).getTemplate().rateHp))
                ((NpcInstance) cha).dropItem(playable.getPlayer(), 6391, 1);
        }
    }
    @Override
    public void onPlayerEnter(Player player) {
        if (_active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.TheFallHarvest.AnnounceEventStarted");
    }
}