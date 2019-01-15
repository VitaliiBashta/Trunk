package l2trunk.scripts.events.glitmedal;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
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
    private static final List<SimpleSpawner> SPAWNS = new ArrayList<>();
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
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("glitter", true)) {
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
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive("glitter", false)) {
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
                new Location(147893, -56622, -2776, 0),
                new Location(-81070, 149960, -3040, 0),
                new Location(82882, 149332, -3464, 49000),
                new Location(44176, -48732, -800, 33000),
                new Location(147920, 25664, -2000, 16384),
                new Location(117498, 76630, -2695, 38000),
                new Location(111776, 221104, -3543, 16384),
                new Location(-84516, 242971, -3730, 34000),
                new Location(-13073, 122801, -3117, 0),
                new Location(-44337, -113669, -224, 0),
                new Location(11281, 15652, -4584, 25000),
                new Location(44122, 50784, -3059, 57344),
                new Location(80986, 54504, -1525, 32768),
                new Location(114733, -178691, -821, 0),
                new Location(18178, 145149, -3054, 7400));

        // 2й эвент кот
        final List<Location> EVENT_MANAGERS2 = List.of(
                new Location(147960, -56584, -2776, 0),
                new Location(-81070, 149860, -3040, 0),
                new Location(82798, 149332, -3464, 49000),
                new Location(44176, -48688, -800, 33000),
                new Location(147985, 25664, -2000, 16384),
                new Location(117459, 76664, -2695, 38000),
                new Location(111724, 221111, -3543, 16384),
                new Location(-84516, 243015, -3730, 34000),
                new Location(-13073, 122841, -3117, 0),
                new Location(-44342, -113726, -240, 0),
                new Location(11327, 15682, -4584, 25000),
                new Location(44157, 50827, -3059, 57344),
                new Location(80986, 54452, -1525, 32768),
                new Location(114719, -178742, -821, 0),
                new Location(18154, 145192, -3054, 7400));

        SpawnNPCs(EVENT_MANAGER_ID1, EVENT_MANAGERS1, SPAWNS);
        SpawnNPCs(EVENT_MANAGER_ID2, EVENT_MANAGERS2, SPAWNS);
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
        if (_active && SimpleCheckDrop(cha, killer)) {
            long count = Util.rollDrop(1, 1, Config.EVENT_GLITTMEDAL_NORMAL_CHANCE * killer.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp * 10000L, true);
            if (count > 0)
                addItem(killer.getPlayer(), EVENT_MEDAL, count, null);
            if (killer.getPlayer().getInventory().getCountOf(Badge_of_Wolf) == 0 && Rnd.chance(Config.EVENT_GLITTMEDAL_GLIT_CHANCE * killer.getPlayer().getRateItems() * ((MonsterInstance) cha).getTemplate().rateHp))
                addItem(killer.getPlayer(), EVENT_GLITTMEDAL, 1, null);
        }
    }

    public void glitchang() {
        Player player = getSelf();
        if (!player.isQuestContinuationPossible(true))
            return;

        if (getItemCount(player, EVENT_MEDAL) >= 1000) {
            removeItem(player, EVENT_MEDAL, 1000, "Glit Exchange");
            addItem(player, EVENT_GLITTMEDAL, 10, "Glit Exchange");
            return;
        }
        player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
    }

    public void medal() {
        Player player = getSelf();
        if (!player.isQuestContinuationPossible(true))
            return;

        if (getItemCount(player, Badge_of_Wolf) >= 1) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_05.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Fox) >= 1) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_04.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Hyena) >= 1) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_03.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Rabbit) >= 1) {
            show("scripts/events/glitmedal/event_col_agent1_q0996_02.htm", player);
            return;
        }

        show("scripts/events/glitmedal/event_col_agent1_q0996_01.htm", player);
    }

    public void medalb() {
        Player player = getSelf();
        if (!player.isQuestContinuationPossible(true))
            return;

        if (getItemCount(player, Badge_of_Wolf) >= 1) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_05.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Fox) >= 1) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_04.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Hyena) >= 1) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_03.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Rabbit) >= 1) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_02.htm", player);
            return;
        }

        show("scripts/events/glitmedal/event_col_agent2_q0996_01.htm", player);
    }

    public void game() {
        Player player = getSelf();
        if (!player.isQuestContinuationPossible(true))
            return;

        if (getItemCount(player, Badge_of_Fox) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 40) {
                show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Hyena) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 20) {
                show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Rabbit) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 10) {
                show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
                return;
            }
            show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
            return;
        } else if (getItemCount(player, EVENT_GLITTMEDAL) >= 5) {
            show("scripts/events/glitmedal/event_col_agent2_q0996_11.htm", player);
            return;
        }

        show("scripts/events/glitmedal/event_col_agent2_q0996_12.htm", player);
    }

    public void gamea() {
        Player player = getSelf();
        if (!player.isQuestContinuationPossible(true))
            return;
        isTalker = Rnd.get(2);

        if (getItemCount(player, Badge_of_Fox) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 40)
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Fox, 1, "gamea");
                    removeItem(player, EVENT_GLITTMEDAL, getItemCount(player, EVENT_GLITTMEDAL), "gamea");
                    addItem(player, Badge_of_Wolf, 1, "gamea");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_24.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 40, "gamea");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Hyena) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 20)
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Hyena, 1, "gamea");
                    removeItem(player, EVENT_GLITTMEDAL, 20, "gamea");
                    addItem(player, Badge_of_Fox, 1, "gamea");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_23.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 20, "gamea");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Rabbit) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 10)
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Rabbit, 1, "gamea");
                    removeItem(player, EVENT_GLITTMEDAL, 10, "gamea");
                    addItem(player, Badge_of_Hyena, 1, "gamea");
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

        if (getItemCount(player, EVENT_GLITTMEDAL) >= 5)
            if (isTalker == 1) {
                removeItem(player, EVENT_GLITTMEDAL, 5, "gamea");
                addItem(player, Badge_of_Rabbit, 1, "gamea");
                show("scripts/events/glitmedal/event_col_agent2_q0996_21.htm", player);
                return;
            } else if (isTalker == 0) {
                removeItem(player, EVENT_GLITTMEDAL, 5, "gamea");
                show("scripts/events/glitmedal/event_col_agent2_q0996_25.htm", player);
                return;
            }
        show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
    }

    // FIXME: нафига две идентичные функции?
    public void gameb() {
        Player player = getSelf();
        if (!player.isQuestContinuationPossible(true))
            return;
        isTalker = Rnd.get(2);

        if (getItemCount(player, Badge_of_Fox) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 40)
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Fox, 1, "gameb");
                    removeItem(player, EVENT_GLITTMEDAL, 40, "gameb");
                    addItem(player, Badge_of_Wolf, 1, "gameb");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_34.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 40, "gameb");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Hyena) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 20)
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Hyena, 1, "gameb");
                    removeItem(player, EVENT_GLITTMEDAL, 20, "gameb");
                    addItem(player, Badge_of_Fox, 1, "gameb");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_33.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 20, "gameb");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        } else if (getItemCount(player, Badge_of_Rabbit) >= 1) {
            if (getItemCount(player, EVENT_GLITTMEDAL) >= 10)
                if (isTalker == 1) {
                    removeItem(player, Badge_of_Rabbit, 1, "gameb");
                    removeItem(player, EVENT_GLITTMEDAL, 10, "gameb");
                    addItem(player, Badge_of_Hyena, 1, "gameb");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_32.htm", player);
                    return;
                } else if (isTalker == 0) {
                    removeItem(player, EVENT_GLITTMEDAL, 10, "gameb");
                    show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                    return;
                }
            show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
            return;
        }

        if (getItemCount(player, EVENT_GLITTMEDAL) >= 5)
            if (isTalker == 1) {
                removeItem(player, EVENT_GLITTMEDAL, 5, "gameb");
                addItem(player, Badge_of_Rabbit, 1, "gameb");
                show("scripts/events/glitmedal/event_col_agent2_q0996_31.htm", player);
                return;
            } else if (isTalker == 0) {
                removeItem(player, EVENT_GLITTMEDAL, 5, "gameb");
                show("scripts/events/glitmedal/event_col_agent2_q0996_35.htm", player);
                return;
            }
        show("scripts/events/glitmedal/event_col_agent2_q0996_26.htm", player);
    }
}