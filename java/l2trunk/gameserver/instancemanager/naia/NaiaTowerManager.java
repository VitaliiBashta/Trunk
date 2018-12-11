package l2trunk.gameserver.instancemanager.naia;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NaiaTowerManager {
    private static final Logger _log = LoggerFactory.getLogger(NaiaTowerManager.class);
    private static final Map<Integer, List<Player>> _groupList = new HashMap<>();
    private static final Map<Integer, List<Player>> _roomsDone = new HashMap<>();
    private static final Map<Integer, Long> _groupTimer = new HashMap<>();
    private static HashMap<Integer, Boolean> lockedRooms;
    private static Map<Integer, List<NpcInstance>> _roomMobs;
    private static List<NpcInstance> _roomMobList;
    private static long _towerAccessible = 0;
    private static int _index = 0;

    public static void init() {
        if (lockedRooms == null) {
            lockedRooms = new HashMap<>();
            for (int i = 18494; i <= 18505; i++)
                lockedRooms.put(i, false);

            _roomMobs = new HashMap<>();
            for (int i = 18494; i <= 18505; i++) {
                _roomMobList = new ArrayList<>();
                _roomMobs.put(i, _roomMobList);
            }

            _log.info("Naia Tower Manager: Loaded 12 rooms");
        }
        ThreadPoolManager.INSTANCE.schedule(new GroupTowerTimer(), 30 * 1000L);
    }

    public static void startNaiaTower(Player leader) {
        if (leader == null)
            return;

        if (_towerAccessible > System.currentTimeMillis())
            return;

        for (Player member : leader.getParty().getMembers())
            member.teleToLocation(new Location(-47271, 246098, -9120));

        addGroupToTower(leader);
        _towerAccessible += 20 * 60 * 1000L;

        ReflectionUtils.getDoor(18250001).openMe();
    }

    private static void addGroupToTower(Player leader) {
        _index = _groupList.keySet().size() + 1;
        _groupList.put(_index, leader.getParty().getMembers());
        _groupTimer.put(_index, System.currentTimeMillis() + 5 * 60 * 1000L);

        leader.sendMessage("The Tower of Naia countdown has begun. You have only 5 minutes to pass each room.");
    }

    public static void updateGroupTimer(Player player) {
        for (int i : _groupList.keySet())
            if (_groupList.get(i).contains(player)) {
                _groupTimer.put(i, System.currentTimeMillis() + 5 * 60 * 1000L);
                player.sendMessage("Group timer has been updated");
                break;
            }
    }

    public static void removeGroupTimer(Player player) {
        for (int i : _groupList.keySet())
            if (_groupList.get(i).contains(player)) {
                _groupList.remove(i);
                _groupTimer.remove(i);
            }
    }

    public static boolean isLegalGroup(Player player) {
        if (_groupList.isEmpty())
            return false;

        for (int i : _groupList.keySet())
            if (_groupList.get(i).contains(player))
                return true;

        return false;
    }

    public static void lockRoom(int npcId) {
        lockedRooms.put(npcId, true);
    }

    public static void unlockRoom(int npcId) {
        lockedRooms.put(npcId, false);
    }

    public static boolean isLockedRoom(int npcId) {
        return lockedRooms.get(npcId);
    }

    public static void addRoomDone(int roomId, Player player) {
        if (player.getParty() != null)
            _roomsDone.put(roomId, player.getParty().getMembers());
    }

    public static boolean isRoomDone(int roomId, Player player) {
        if (_roomsDone.isEmpty())
            return false;

        if (_roomsDone.get(roomId) == null || _roomsDone.get(roomId).isEmpty())
            return false;

        return _roomsDone.get(roomId).contains(player);

    }

    public static void addMobsToRoom(int roomId, List<NpcInstance> mob) {
        _roomMobs.put(roomId, mob);
    }

    public static List<NpcInstance> getRoomMobs(int roomId) {
        return _roomMobs.get(roomId);
    }

    public static void removeRoomMobs(int roomId) {
        _roomMobs.get(roomId).clear();
    }

    private static class GroupTowerTimer extends RunnableImpl {
        @Override
        public void runImpl() {
            ThreadPoolManager.INSTANCE.schedule(new GroupTowerTimer(), 30 * 1000L);
            if (!_groupList.isEmpty() && !_groupTimer.isEmpty())
                for (int i : _groupTimer.keySet())
                    if (_groupTimer.get(i) < System.currentTimeMillis()) {
                        for (Player kicked : _groupList.get(i)) {
                            kicked.teleToLocation(new Location(17656, 244328, 11595));
                            kicked.sendMessage("The time has expired. You cannot stay in Tower of Naia any longer");
                        }
                        _groupList.remove(i);
                        _groupTimer.remove(i);
                    }
        }
    }

}