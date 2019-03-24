package l2trunk.gameserver.instancemanager.naia;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class NaiaTowerManager {
    private static final Logger _log = LoggerFactory.getLogger(NaiaTowerManager.class);
    private static final Map<Integer, Collection<Player>> GROUP_LIST = new HashMap<>();
    private static final Map<Integer, Collection<Player>> ROOMS_DONE = new HashMap<>();
    private static final Map<Integer, Long> GROUP_TIMER = new HashMap<>();
    private static HashMap<Integer, Boolean> lockedRooms;
    private static Map<Integer, List<NpcInstance>> roomMobs;
    private static List<NpcInstance> roomMobList;
    private static long towerAccessible = 0;
    private static int index = 0;

    public static void init() {
        if (lockedRooms == null) {
            lockedRooms = new HashMap<>();
            for (int i = 18494; i <= 18505; i++)
                lockedRooms.put(i, false);

            roomMobs = new HashMap<>();
            for (int i = 18494; i <= 18505; i++) {
                roomMobList = new ArrayList<>();
                roomMobs.put(i, roomMobList);
            }

            _log.info("Naia Tower Manager: Loaded 12 rooms");
        }
        ThreadPoolManager.INSTANCE.schedule(new GroupTowerTimer(), 30 * 1000L);
    }

    public static void startNaiaTower(Player leader) {
        if (leader == null)
            return;

        if (towerAccessible > System.currentTimeMillis())
            return;

        leader.getParty().getMembers().forEach(member ->
            member.teleToLocation( Location.of(-47271, 246098, -9120)));

        addGroupToTower(leader);
        towerAccessible += 20 * 60 * 1000L;

        ReflectionUtils.getDoor(18250001).openMe();
    }

    private static void addGroupToTower(Player leader) {
        index = GROUP_LIST.keySet().size() + 1;
        GROUP_LIST.put(index, leader.getParty().getMembers());
        GROUP_TIMER.put(index, System.currentTimeMillis() + 5 * 60 * 1000L);

        leader.sendMessage("The Tower of Naia countdown has begun. You have only 5 minutes to pass each room.");
    }

    public static void updateGroupTimer(Player player) {
        for (int i : GROUP_LIST.keySet())
            if (GROUP_LIST.get(i).contains(player)) {
                GROUP_TIMER.put(i, System.currentTimeMillis() + 5 * 60 * 1000L);
                player.sendMessage("Group timer has been updated");
                break;
            }
    }

    public static void removeGroupTimer(Player player) {
        for (int i : GROUP_LIST.keySet())
            if (GROUP_LIST.get(i).contains(player)) {
                GROUP_LIST.remove(i);
                GROUP_TIMER.remove(i);
            }
    }

    public static boolean isLegalGroup(Player player) {
        if (GROUP_LIST.isEmpty())
            return false;

        for (int i : GROUP_LIST.keySet())
            if (GROUP_LIST.get(i).contains(player))
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
            ROOMS_DONE.put(roomId, player.getParty().getMembers());
    }

    public static boolean isRoomDone(int roomId, Player player) {
        if (ROOMS_DONE.isEmpty())
            return false;

        if (ROOMS_DONE.get(roomId) == null || ROOMS_DONE.get(roomId).isEmpty())
            return false;

        return ROOMS_DONE.get(roomId).contains(player);

    }

    public static void addMobsToRoom(int roomId, List<NpcInstance> mob) {
        roomMobs.put(roomId, mob);
    }

    public static List<NpcInstance> getRoomMobs(int roomId) {
        return roomMobs.get(roomId);
    }

    public static void removeRoomMobs(int roomId) {
        roomMobs.get(roomId).clear();
    }

    private static class GroupTowerTimer extends RunnableImpl {
        @Override
        public void runImpl() {
            ThreadPoolManager.INSTANCE.schedule(new GroupTowerTimer(), 30 * 1000L);
            if (!GROUP_LIST.isEmpty() && !GROUP_TIMER.isEmpty())
                for (int i : GROUP_TIMER.keySet())
                    if (GROUP_TIMER.get(i) < System.currentTimeMillis()) {
                        for (Player kicked : GROUP_LIST.get(i)) {
                            kicked.teleToLocation( 17656, 244328, 11595);
                            kicked.sendMessage("The time has expired. You cannot stay in Tower of Naia any longer");
                        }
                        GROUP_LIST.remove(i);
                        GROUP_TIMER.remove(i);
                    }
        }
    }

}