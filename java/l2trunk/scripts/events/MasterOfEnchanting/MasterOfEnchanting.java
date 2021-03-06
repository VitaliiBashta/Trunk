package l2trunk.scripts.events.MasterOfEnchanting;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class MasterOfEnchanting extends Functions implements ScriptFile, OnPlayerEnterListener {
    private static final Logger LOG = LoggerFactory.getLogger(MasterOfEnchanting.class);
    private static final String EVENT_NAME = "MasterOfEnchanting";
    private static final int EVENT_MANAGER_ID = 32599;
    private static List<SimpleSpawner> _spawns = new ArrayList<>();
    private static boolean _active = false;
    private static final int[][] _herbdrop = {{20000, 100}, //Spicy Kimchee
            {20001, 100}, //Spicy Kimchee
            {20002, 100}, //Spicy Kimchee
            {20003, 100}}; //Sweet-and-Sour White Kimchee
    private static final int[][] _energydrop = {{20004, 30}, //Energy Ginseng
            {20005, 100}}; //Energy Red Ginseng

    private void spawnEventManagers() {
        final List<Location> EVENT_MANAGERS = List.of(
                Location.of(-119494, 44882, 360, 24576), //Kamael Village
                Location.of(86865, -142915, -1336, 26000));

        _spawns = SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS);
    }


    private void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
    }

    private static boolean isActive() {
        return isActive(EVENT_NAME);
    }

    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive(EVENT_NAME, true)) {
            spawnEventManagers();
            System.out.println("Event: Master of Enchanting started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.MasOfEnch.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Master of Enchanting' already started.");

        _active = true;
        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive(EVENT_NAME, false)) {
            unSpawnEventManagers();
            System.out.println("Event: Master of Enchanting stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.MasOfEnch.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'Master of Enchanting' not started.");

        _active = false;
        show("html/admin/events/events.htm", player);
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            spawnEventManagers();
            LOG.info("Loaded Event: Master of Enchanting [state: activated]");
        } else
            LOG.info("Loaded Event: Master of Enchanting [state: deactivated]");
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
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.MasOfEnch.AnnounceEventStarted");
    }

}