package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Format:(ch) d d [dsdddd]
 */
public class ExPartyRoomMember extends L2GameServerPacket {
    private final int _type;
    private List<PartyRoomMemberInfo> _members = Collections.emptyList();

    public ExPartyRoomMember(MatchingRoom room, Player activeChar) {
        _type = room.getMemberType(activeChar);
        _members = new ArrayList<>(room.getPlayers().size());
        for (Player $member : room.getPlayers())
            _members.add(new PartyRoomMemberInfo($member, room.getMemberType($member)));
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x08);
        writeD(_type);
        writeD(_members.size());
        for (PartyRoomMemberInfo member_info : _members) {
            writeD(member_info.objectId);
            writeS(member_info.name);
            writeD(member_info.classId);
            writeD(member_info.level);
            writeD(member_info.location);
            writeD(member_info.memberType);
            writeD(member_info.instanceReuses.length);
            for (int i : member_info.instanceReuses)
                writeD(i);
        }
    }

    static class PartyRoomMemberInfo {
        final int objectId;
        final int classId;
        final int level;
        final int location;
        final int memberType;
        final String name;
        final int[] instanceReuses;

        PartyRoomMemberInfo(Player member, int type) {
            objectId = member.getObjectId();
            name = member.getName();
            classId = member.getClassId().ordinal();
            level = member.getLevel();
            location = MatchingRoomManager.getInstance().getLocation(member);
            memberType = type;
            instanceReuses = ArrayUtils.toArray(member.getInstanceReuses().keySet());
        }
    }
}