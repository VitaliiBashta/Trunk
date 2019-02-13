package l2trunk.scripts.events.FreyaEvent;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class FreyaEvent extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final int EVENT_MANAGER_ID = 13296;
    private static final int ADENA = 57;
    private static final int GIFT_RECEIVE_DELAY = 20;
    private static final int GIFT_ID = 15440;
    private static final int GIFT_PRICE = 1;
    private static final int DROP_CHANCE = 55;
    private static final List<Integer> DROP_LIST = List.of(
            17130, 17131, 17132, 17133, 17134, 17135, 17136, 17137);
    
    private static final String _name = "Freya Celebration";
    private static final String _msgStarted = "scripts.events.FreyaEvent.AnnounceEventStarted";
    private static final String _msgEnded = "scripts.events.FreyaEvent.AnnounceEventStoped";
    private static final Logger _log = LoggerFactory.getLogger(FreyaEvent.class);
    private static List<SimpleSpawner> _spawns = new ArrayList<>();

    private static boolean _active = false;

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
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
           List<Location> EVENT_MANAGERS = List.of(
                Location.of(16111, 142850, -2707, 16000),
                Location.of(17275, 145000, -3037, 25000),
                Location.of(83037, 149324, -3470, 44000),
                Location.of(82145, 148609, -3468, 0),
                Location.of(81755, 146487, -3534, 32768),
                Location.of(-81031, 150038, -3045, 0),
                Location.of(-83156, 150994, -3130, 0),
                Location.of(-13727, 122117, -2990, 16384),
                Location.of(-14129, 123869, -3118, 40959),
                Location.of(-84411, 244813, -3730, 57343),
                Location.of(-84023, 243051, -3730, 4096),
                Location.of(46908, 50856, -2997, 8192),
                Location.of(45538, 48357, -3061, 18000),
                Location.of(9929, 16324, -4576, 62999),
                Location.of(11546, 17599, -4586, 46900),
                Location.of(81987, 53723, -1497, 0),
                Location.of(81083, 56118, -1562, 32768),
                Location.of(147200, 25614, -2014, 16384),
                Location.of(148557, 26806, -2206, 32768),
                Location.of(117356, 76708, -2695, 49151),
                Location.of(115887, 76382, -2714, 0),
                Location.of(-117239, 46842, 367, 49151),
                Location.of(-119494, 44882, 367, 24576),
                Location.of(111004, 218928, -3544, 16384),
                Location.of(108426, 221876, -3600, 49151),
                Location.of(-45278, -112766, -241, 0),
                Location.of(-45372, -114104, -241, 16384),
                Location.of(115096, -178370, -891, 0),
                Location.of(116199, -182694, -1506, 0),
                Location.of(86865, -142915, -1341, 26000),
                Location.of(85584, -142490, -1343, 0),
                Location.of(147421, -55435, -2736, 49151),
                Location.of(148206, -55786, -2782, 61439),
                Location.of(43165, -48461, -797, 17000),
                Location.of(43966, -47709, -798, 49999));

        _spawns = SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(_spawns);
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
            Playable playable = (Playable) killer;
            if (_active && simpleCheckDrop(cha, playable)) {
                int itemId = Rnd.get(DROP_LIST);
                if (Rnd.chance(DROP_CHANCE))
                    ((NpcInstance) cha).dropItem(playable.getPlayer(), itemId, 1);
            }
        }
    }

    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive(_name, true)) {
            spawnEventManagers();
            System.out.println("Event '" + _name + "' started.");
            Announcements.INSTANCE.announceByCustomMessage(_msgStarted);
        } else
            player.sendMessage("Event '" + _name + "' already started.");

        _active = true;

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive(_name, false)) {
            unSpawnEventManagers();
            System.out.println("Event '" + _name + "' stopped.");
            Announcements.INSTANCE.announceByCustomMessage(_msgEnded);
        } else
            player.sendMessage("Event '" + _name + "' not started.");

        _active = false;

        show("admin/events/events.htm", player);
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, _msgStarted);
    }

    public void receiveGift() {
        long _remaining_time;
        long _reuse_time = GIFT_RECEIVE_DELAY * 60 * 60 * 1000;
        long _curr_time = System.currentTimeMillis();

        if (player.isVarSet("FreyaCelebration") )
            _remaining_time = _curr_time - player.getVarLong("FreyaCelebration");
        else
            _remaining_time = _reuse_time;

        if (_remaining_time >= _reuse_time) {
            if (player.haveAdena(GIFT_PRICE)) {
                player.reduceAdena( GIFT_PRICE, "FreyaEvent");
                addItem(player, GIFT_ID, 1);
                player.setVar("FreyaCelebration", _curr_time);
            } else
                player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addNumber(GIFT_PRICE));
        } else {
            int hours = (int) (_reuse_time - _remaining_time) / 3600000;
            int minutes = (int) (_reuse_time - _remaining_time) % 3600000 / 60000;
            if (hours > 0)
                player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
            else if (minutes > 0)
                player.sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
            else if (player.haveAdena( GIFT_PRICE)) {
                removeItem(player, ADENA, GIFT_PRICE, "FreyaEvent");
                addItem(player, GIFT_ID, 1);
                player.setVar("FreyaCelebration", _curr_time);
            } else
                player.sendPacket(new SystemMessage(SystemMessage._2_UNITS_OF_THE_ITEM_S1_IS_REQUIRED).addNumber(GIFT_PRICE));
        }
    }
}