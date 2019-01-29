package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Format:(ch) d [sdd]
 */
public final class ExListPartyMatchingWaitingRoom extends L2GameServerPacket {
    private final int _fullSize;
    private List<PartyMatchingWaitingInfo> _waitingList;

    public ExListPartyMatchingWaitingRoom(Player searcher, int minLevel, int maxLevel, int page, List<Integer> classes) {
        int first = (page - 1) * 64;
        int firstNot = page * 64;
        int i = 0;

        List<Player> temp = MatchingRoomManager.INSTANCE.getWaitingList(minLevel, maxLevel, classes);
        _fullSize = temp.size();

        _waitingList = new ArrayList<>(_fullSize);
        for (Player pc : temp) {
            if (i < first || i >= firstNot)
                continue;
            _waitingList.add(new PartyMatchingWaitingInfo(pc));
            i++;
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0x36);

        writeD(_fullSize);
        writeD(_waitingList.size());
        for (PartyMatchingWaitingInfo waiting_info : _waitingList) {
            writeS(waiting_info.name);
            writeD(waiting_info.classId);
            writeD(waiting_info.level);
            writeD(waiting_info.currentInstance);
            writeD(waiting_info.instanceReuses.size());
            waiting_info.instanceReuses.forEach(this::writeD);
        }
    }

    static class PartyMatchingWaitingInfo {
        final int classId;
        final int level;
        final int currentInstance;
        final String name;
        final Collection<Integer> instanceReuses;

        PartyMatchingWaitingInfo(Player member) {
            name = member.getName();
            classId = member.getClassId().id();
            level = member.getLevel();
            Reflection ref = member.getReflection();
            currentInstance = ref == null ? 0 : ref.getInstancedZoneId();
            instanceReuses = member.getInstanceReuses().keySet();
        }
    }
}