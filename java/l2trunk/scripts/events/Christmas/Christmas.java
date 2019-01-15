package l2trunk.scripts.events.Christmas;

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
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.events.EventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Christmas extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final int EVENT_MANAGER_ID = 31863;
    private static final int CTREE_ID = 13006;
    private static final Logger _log = LoggerFactory.getLogger(Christmas.class);

    private static final int[][] _dropdata = {
            // Item, chance
            {5556, 20}, //Star Ornament 2%
            {5557, 20}, //Bead Ornament 2%
            {5558, 50}, //Fir Tree Branch 5%
            {5559, 5}, //Flower Pot 0.5%
            /*
            // Музыкальные кристаллы 0.2%
            { 5562, 2 },
            { 5563, 2 },
            { 5564, 2 },
            { 5565, 2 },
            { 5566, 2 },
            { 5583, 2 },
            { 5584, 2 },
            { 5585, 2 },
            { 5586, 2 },
            { 5587, 2 },
            { 4411, 2 },
            { 4412, 2 },
            { 4413, 2 },
            { 4414, 2 },
            { 4415, 2 },
            { 4416, 2 },
            { 4417, 2 },
            { 5010, 2 },
            { 7061, 2 },
            { 7062, 2 },
            { 6903, 2 },
            { 8555, 2 }
             */
    };

    private static final List<SimpleSpawner> SPAWNER_LIST = new ArrayList<>();

    private static boolean active = false;

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            active = true;
            spawnEventManagers();
            _log.info("Loaded Event: Christmas [state: activated]");
        } else
            _log.info("Loaded Event: Christmas [state: deactivated]");
    }

    private static boolean isActive() {
        return isActive("Christmas");
    }

    /**
     * Запускает эвент
     */
    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("Christmas", true)) {
            spawnEventManagers();
            System.out.println("Event 'Christmas' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.Christmas.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Christmas' already started.");

        active = true;

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive("Christmas", false)) {
            unSpawnEventManagers();
            System.out.println("Event 'Christmas' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.Christmas.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'Christmas' not started.");

        active = false;

        show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS, SPAWNER_LIST);
        SpawnNPCs(CTREE_ID, EventsConfig.CTREES, SPAWNER_LIST);
    }


    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNER_LIST);
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
        if (active && SimpleCheckDrop(cha, killer)) {
            int dropCounter = 0;
            for (int[] drop : _dropdata)
                if (Rnd.chance(drop[1] * killer.getPlayer().getRateItems() * Config.RATE_DROP_ITEMS * 0.1)) {
                    dropCounter++;
                    ((NpcInstance) cha).dropItem(killer.getPlayer(), drop[0], 1);

                    // Из одного моба выпадет не более 3-х эвентовых предметов
                    if (dropCounter > 2)
                        break;
                }
        }
    }

    public void exchange(String[] var) {
        Player player = getSelf();

        if (!player.isQuestContinuationPossible(true))
            return;

        if (player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
            return;

        if (var[0].equalsIgnoreCase("0")) {
            if (getItemCount(player, 5556) >= 4 && getItemCount(player, 5557) >= 4 && getItemCount(player, 5558) >= 10 && getItemCount(player, 5559) >= 1) {
                removeItem(player, 5556, 4, "Christmas");
                removeItem(player, 5557, 4, "Christmas");
                removeItem(player, 5558, 10, "Christmas");
                removeItem(player, 5559, 1, "Christmas");
                addItem(player, 5560, 1, "Christmas"); // Christmas Tree
                return;
            }
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
        if (var[0].equalsIgnoreCase("1")) {
            if (getItemCount(player, 5560) >= 10) {
                removeItem(player, 5560, 10, "Christmas");
                addItem(player, 5561, 1, "Christmas"); // Special Christmas Tree
                return;
            }
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
        if (var[0].equalsIgnoreCase("2")) {
            if (getItemCount(player, 5560) >= 10) {
                removeItem(player, 5560, 10, "Christmas");
                addItem(player, 7836, 1, "Christmas"); // Santa's Hat
                return;
            }
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
        if (var[0].equalsIgnoreCase("3")) {
            if (getItemCount(player, 5560) >= 10) {
                removeItem(player, 5560, 10, "Christmas");
                addItem(player, 8936, 1, "Christmas"); // Santa's Antlers
                return;
            }
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
        if (var[0].equalsIgnoreCase("4")) {
            if (getItemCount(player, 5560) >= 20) {
                removeItem(player, 5560, 20, "Christmas");
                addItem(player, 10606, 1, "Christmas"); // Agathion Seal Bracelet - Rudolph
                return;
            }
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.Christmas.AnnounceEventStarted");
    }
}