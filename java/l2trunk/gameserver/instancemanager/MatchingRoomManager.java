package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;
import l2trunk.gameserver.templates.mapregion.RestartArea;
import l2trunk.gameserver.templates.mapregion.RestartPoint;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


public enum MatchingRoomManager {
    INSTANCE;
    private final RoomsHolder[] _holder = new RoomsHolder[2];
    private final Set<Player> players = new CopyOnWriteArraySet<>();

    MatchingRoomManager() {
        _holder[MatchingRoom.PARTY_MATCHING] = new RoomsHolder();
        _holder[MatchingRoom.CC_MATCHING] = new RoomsHolder();
    }

    public void addToWaitingList(Player player) {
        players.add(player);
    }

    public void removeFromWaitingList(Player player) {
        players.remove(player);
    }

    public List<Player> getWaitingList(int minLevel, int maxLevel, List<Integer> classes) {
        return players.stream()
                .filter(m -> m.getLevel() >= minLevel)
                .filter(m -> m.getLevel() <= maxLevel)
                .filter(m -> (classes.size() == 0 || classes.contains(m.getClassId().id)))
                .collect(Collectors.toList());
    }

    public List<MatchingRoom> getMatchingRooms(int type, int region, boolean allLevels, Player activeChar) {
        List<MatchingRoom> res = new ArrayList<>();
        for (MatchingRoom room : _holder[type]._rooms.values()) {
            if (region > 0 && room.getLocationId() != region)
                continue;
            else if (region == -2 && room.getLocationId() != MatchingRoomManager.INSTANCE.getLocation(activeChar))
                continue;
            if (!allLevels && (room.getMinLevel() > activeChar.getLevel() || room.getMaxLevel() < activeChar.getLevel()))
                continue;
            res.add(room);
        }
        return res;
    }

    public int addMatchingRoom(MatchingRoom r) {
        return _holder[r.getType()].addRoom(r);
    }

    public void removeMatchingRoom(MatchingRoom r) {
        _holder[r.getType()]._rooms.remove(r.getId());
    }

    public MatchingRoom getMatchingRoom(int type, int id) {
        return _holder[type]._rooms.get(id);
    }

    public int getLocation(Player player) {
        if (player == null)
            return 0;

        RestartArea ra = MapRegionHolder.getInstance().getRegionData(RestartArea.class, player);
        if (ra != null) {
            RestartPoint rp = ra.getRestartPoint().get(player.getRace());
            return rp.bbs;
        }

        return 0;
    }

    private class RoomsHolder {
        private final Map<Integer, MatchingRoom> _rooms = new HashMap<>();
        private int _id = 1;

        int addRoom(MatchingRoom r) {
            int val = _id++;
            _rooms.put(val, r);
            return val;
        }
    }
}
