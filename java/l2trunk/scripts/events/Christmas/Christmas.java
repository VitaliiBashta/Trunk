package l2trunk.scripts.events.Christmas;

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
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.events.EventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Christmas extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final int EVENT_MANAGER_ID = 31863;
    private static final int CTREE_ID = 13006;
    private static final Logger _log = LoggerFactory.getLogger(Christmas.class);

    private static final Map<Integer, Integer> DROPDATA = Map.of(
            // Item, chance
            5556, 20, //Star Ornament 2%
            5557, 20, //Bead Ornament 2%
            5558, 50, //Fir Tree Branch 5%
            5559, 5); //Flower Pot 0.5%

    private static List<SimpleSpawner> SPAWNER_LIST;

    private static boolean active = false;

    private static boolean isActive() {
        return isActive("Christmas");
    }

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

    /**
     * Запускает эвент
     */
    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive("Christmas", true)) {
            spawnEventManagers();
            System.out.println("Event 'Christmas' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.Christmas.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Christmas' already started.");

        active = true;

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive("Christmas", false)) {
            unSpawnEventManagers();
            System.out.println("Event 'Christmas' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.Christmas.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'Christmas' not started.");

        active = false;

        show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        SPAWNER_LIST = SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS);
        SPAWNER_LIST.addAll(SpawnNPCs(CTREE_ID, EventsConfig.CTREES));
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
        if (killer instanceof Playable) {
            Player player = ((Playable) killer).getPlayer();
            if (active && simpleCheckDrop(cha, player)) {
                DROPDATA.forEach((k, v) -> {
                    if (Rnd.chance(v * player.getRateItems() * Config.RATE_DROP_ITEMS * 0.1))
                        ((NpcInstance) cha).dropItem(player, k, 1);
                });
            }
        }
    }

    public void exchange(String[] var) {
        if (!player.isQuestContinuationPossible(true))
            return;

        if (player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
            return;

        switch (var[0]) {
            case "0":
                if (player.haveItem(5556, 4) && player.haveItem(5557, 4)
                        && player.haveItem(5558, 10) && player.haveItem(5559)) {
                    removeItem(player, 5556, 4, "Christmas");
                    removeItem(player, 5557, 4, "Christmas");
                    removeItem(player, 5558, 10, "Christmas");
                    removeItem(player, 5559, 1, "Christmas");
                    addItem(player, 5560, 1); // Christmas Tree
                    return;
                }
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                break;
            case "1":
                if (player.haveItem(5560, 10)) {
                    removeItem(player, 5560, 10, "Christmas");
                    addItem(player, 5561, 1); // Special Christmas Tree
                    return;
                }
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                break;
            case "2":
                if (player.haveItem(5560, 10)) {
                    removeItem(player, 5560, 10, "Christmas");
                    addItem(player, 7836, 1); // Santa's Hat
                    return;
                }
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                break;
            case "3":
                if (player.haveItem(5560, 10)) {
                    removeItem(player, 5560, 10, "Christmas");
                    addItem(player, 8936, 1); // Santa's Antlers
                    return;
                }
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                break;
            case "4":
                if (player.haveItem(5560, 20)) {
                    removeItem(player, 5560, 20, "Christmas");
                    addItem(player, 10606, 1); // Agathion Seal Bracelet - Rudolph
                    return;
                }
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                break;
        }
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.Christmas.AnnounceEventStarted");
    }
}