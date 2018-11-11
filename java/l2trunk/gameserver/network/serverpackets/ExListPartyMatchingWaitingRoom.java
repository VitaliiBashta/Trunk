package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Format:(ch) d [sdd]
 */
public class ExListPartyMatchingWaitingRoom extends L2GameServerPacket {
    private final int _fullSize;
    private List<PartyMatchingWaitingInfo> _waitingList = Collections.emptyList();

    public ExListPartyMatchingWaitingRoom(Player searcher, int minLevel, int maxLevel, int page, int[] classes) {
        int first = (page - 1) * 64;
        int firstNot = page * 64;
        int i = 0;

        List<Player> temp = MatchingRoomManager.getInstance().getWaitingList(minLevel, maxLevel, classes);
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
            writeD(waiting_info.instanceReuses.length);
            for (int i : waiting_info.instanceReuses)
                writeD(i);
        }
    }

    static class PartyMatchingWaitingInfo {
        final int classId;
        final int level;
        final int currentInstance;
        final String name;
        final int[] instanceReuses;

        PartyMatchingWaitingInfo(Player member) {
            name = member.getName();
            classId = member.getClassId().getId();
            level = member.getLevel();
            Reflection ref = member.getReflection();
            currentInstance = ref == null ? 0 : ref.getInstancedZoneId();
            instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
        }
    }
}