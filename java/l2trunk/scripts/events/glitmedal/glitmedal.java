package l2trunk.scripts.events.glitmedal;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class glitmedal extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final int EVENT_MANAGER_ID1 = 31228; // Roy
    private static final int EVENT_MANAGER_ID2 = 31229; // Winnie

    private static final Logger LOG = LoggerFactory.getLogger(glitmedal.class);
    // Медали
    private static final int EVENT_MEDAL = 6392;
    private static final int EVENT_GLITTMEDAL = 6393;
    private static final int Badge_of_Rabbit = 6399;
    private static final int Badge_of_Hyena = 6400;
    private static final int Badge_of_Fox = 6401;
    private static final int Badge_of_Wolf = 6402;
    private static List<SimpleSpawner> SPAWNS = new ArrayList<>();
    private static boolean _active = false;
    private static boolean MultiSellLoaded = false;
    private final List<Path> multiSellFiles = List.of(
            Config.DATAPACK_ROOT.resolve("data/multisell/events/glitmedal/502.xml"),
            Config.DATAPACK_ROOT.resolve("data/multisell/events/glitmedal/503.xml"),
            Config.DATAPACK_ROOT.resolve("data/multisell/events/glitmedal/504.xml"),
            Config.DATAPACK_ROOT.resolve("data/multisell/events/glitmedal/505.xml"),
            Config.DATAPACK_ROOT.resolve("data/multisell/events/glitmedal/506.xml"));
    // Для временного статуса который выдается в игре рандомно либо 0 либо 1
    private int isTalker;

    private static boolean isActive() {
        return isActive("glitter");
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            loadMultiSell();
            spawnEventManagers();
            LOG.info("Loaded Event: L2 Medal Collection Event [state: activated]");
        } else
            LOG.info("Loaded Event: L2 Medal Collection Event [state: deactivated]");
    }

    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive("glitter", true)) {
            loadMultiSell();
            spawnEventManagers();
            System.out.println("Event 'L2 Medal Collection Event' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.glitmedal.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'L2 Medal Collection Event' already started.");

        _active = true;

        show("admin/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive("glitter", false)) {
            unSpawnEventManagers();
            System.out.println("Event 'L2 Medal Collection Event' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.glitmedal.AnnounceEventStoped");
        } else
            player.sendMessage("Event 'L2 Medal Collection Event' not started.");

        _active = false;

        show("admin/events.htm", player);
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.glitmedal.AnnounceEventStarted");
    }

    private void spawnEventManagers() {
        // 1й эвент кот
        final List<Location> EVENT_MANAGERS1 = List.of(
                Location.of(147893, -56622, -2776, 0),
                Location.of(-81070, 149960, -3040, 0),
                Location.of(82882, 149332, -3464, 49000),
                Location.of(44176, -48732, -800, 33000),
                Location.of(147920, 25664, -2000, 16384),
                Location.of(117498, 76630, -2695, 38000),
                Location.of(111776, 221104, -3543, 16384),
                Location.of(-84516, 242971, -3730, 34000),
                Location.of(-13073, 122801, -3117, 0),
                Location.of(-44337, -113669, -224, 0),
                Location.of(11281, 15652, -4584, 25000),
                Location.of(44122, 50784, -3059, 57344),
                Location.of(80986, 54504, -1525, 32768),
                Location.of(114733, -178691, -821, 0),
                Location.of(18178, 145149, -3054, 7400));

        // 2й эвент кот
        final List<Location> EVENT_MANAGERS2 = List.of(
                Location.of(147960, -56584, -2776, 0),
                Location.of(-81070, 149860, -3040, 0),
                Location.of(82798, 149332, -3464, 49000),
                Location.of(44176, -48688, -800, 33000),
                Location.of(147985, 25664, -2000, 16384),
                Location.of(117459, 76664, -2695, 38000),
                Location.of(111724, 221111, -3543, 16384),
                Location.of(-84516, 243015, -3730, 34000),
                Location.of(-13073, 122841, -3117, 0),
                Location.of(-44342, -113726, -240, 0),
                Location.of(11327, 15682, -4584, 25000),
                Location.of(44157, 50827, -3059, 57344),
                Location.of(80986, 54452, -1525, 32768),
                Location.of(114719, -178742, -821, 0),
                Location.of(18154, 145192, -3054, 7400));

        SPAWNS = SpawnNPCs(EVENT_MANAGER_ID1, EVENT_MANAGERS1);
        SPAWNS.addAll(SpawnNPCs(EVENT_MANAGER_ID2, EVENT_MANAGERS2));
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNS);
    }

    private void loadMultiSell() {
        if (MultiSellLoaded)
            return;
        multiSellFiles.forEach(MultiSellHolder.INSTANCE::parseFile);
        MultiSellLoaded = true;
    }

    @Override
    public void onReload() {
        unSpawnEventManagers();
        if (MultiSellLoaded) {
            multiSellFiles.forEach(MultiSellHolder.INSTANCE::remove);
            MultiSellLoaded = false;
        }
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (killer instanceof Playable) {
            Playable playable = (Playable) killer;
            if (_active && simpleCheckDrop(cha, playable)) {
                long count = Util.rollDrop(1, 1, Config.EVENT_GLITTMEDAL_NORMAL_CHANCE * playable.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp * 10000L, true);
                if (count > 0)
                    addItem(playable.getPlayer(), EVENT_MEDAL, count);
                if (playable.getPlayer().getInventory().getCountOf(Badge_of_Wolf) == 0 && Rnd.chance(Config.EVENT_GLITTMEDAL_GLIT_CHANCE * playable.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp))
                    addItem(playable.getPlayer(), EVENT_GLITTMEDAL, 1);
            }
        }
    }

    public void glitchang() {
        if (!player.isQuestContinuationPossible(true))
            return;

        if (player.haveItem( EVENT_MEDAL, 1000)) {
            removeItem(player, EVENT_MEDAL, 1000, "Glit Exchange");
            addItem(player, EVENT_GLITTMEDAL, 10);
            return;
        }
        player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
    }

    public void medal() {
        if (!player.isQuestContinuationPossible(true))
            return;

        if (player.haveItem(Badge_of_Wolf) ) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_05.htm", player);
            return;
        } else if (player.haveItem(Badge_of_Fox)) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_04.htm", player);
            return;
        } else if (player.haveItem(Badge_of_Hyena)) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_03.htm", player);
            return;
        } else if (player.haveItem( Badge_of_Rabbit) ) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_02.htm", player);
            return;
        }

        show("scripts/events/glitmedal/event_col_agent1_q0996_01.htm", player);
    }

    public void medalb() {
        if (!player.isQuestContinuationPossible(true))
            return;

        if (player.haveItem(Badge_of_Wolf) ) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_05.htm", player);
            return;
        } else if (player.haveItem( Badge_of_Fox)) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_04.htm", player);
            return;
        } else if (player.haveItem( Badge_of_Hyena) ) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_03.htm", player);
            return;
        } else if (player.haveItem(Badge_of_Rabbit) ) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_02.htm", player);
            return;
        }

        show("scripts/events/glitmedal/event_col_agent2_q0996_01.htm", player);
    }

    public void game() {
        if (!player.isQuestContinuationPossible(true))
            return;

        if (player.haveItem( Badge_of_Fox) ) {
            if (player.haveItem( EVENT_GLITTMEDAL, 40)) {
                show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        } else if (player.haveItem( Badge_of_Hyena) ) {
            if (player.haveItem(EVENT_GLITTMEDAL, 20)) {
                show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        } else if (player.haveItem( Badge_of_Rabbit) ) {
            if (player.haveItem(EVENT_GLITTMEDAL, 10)) {
                show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        } else if (player.haveItem( EVENT_GLITTMEDAL,5)) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
            return;
        }

        show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
    }

    public void gamea() {
        if (!player.isQuestContinuationPossible(true))
            return;
        isTalker = Rnd.get(2);

        if (player.haveItem(Badge_of_Fox)) {
            if (player.haveItem( EVENT_GLITTMEDAL,  40))
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Fox, 1, "gamea");
                    removeItem(player, EVENT_GLITTMEDAL, player.inventory.getCountOf(EVENT_GLITTMEDAL), "gamea");
                    addItem(player, Badge_of_Wolf, 1);
                    show("scripts/events/glitmedal/event_col_agent2_q0996_24.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 40, "gamea");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        } else if (player.haveItem( Badge_of_Hyena) ) {
            if (player.haveItem( EVENT_GLITTMEDAL,20))
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Hyena, 1, "gamea");
                    removeItem(player, EVENT_GLITTMEDAL, 20, "gamea");
                    addItem(player, Badge_of_Fox, 1);
                    show("scripts/events/glitmedal/event_col_agent2_q0996_23.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 20, "gamea");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        } else if (player.haveItem( Badge_of_Rabbit)) {
            if (player.haveItem(EVENT_GLITTMEDAL, 10))
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Rabbit,  "gamea");
                    removeItem(player, EVENT_GLITTMEDAL, 10, "gamea");
                    addItem(player, Badge_of_Hyena);
                    show("scripts/events/glitmedal/event_col_agent2_q0996_22.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 10, "gamea");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }

        if (player.haveItem( EVENT_GLITTMEDAL, 5))
            if (isTalker == 1) {
                removeItem(player, EVENT_GLITTMEDAL, 5, "gamea");
                addItem(player, Badge_of_Rabbit, 1);
                show("scripts/events/glitmedal/event_col_agent2_q0996_21.htm", player);
                return;
            } else if (isTalker == 0) {
                removeItem(player, EVENT_GLITTMEDAL, 5, "gamea");
                show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                return;
            }
        show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
    }

}