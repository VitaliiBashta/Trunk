package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Format:(ch) d d [dsdddd]
 */
public final class ExPartyRoomMember extends L2GameServerPacket {
    private final int type;
    private List<PartyRoomMemberInfo> members;

    public ExPartyRoomMember(MatchingRoom room, Player activeChar) {
        type = room.getMemberType(activeChar);
        members = new ArrayList<>(room.getPlayers().size());
        room.getPlayers().forEach(member -> members.add(new PartyRoomMemberInfo(member, room.getMemberType(member))));
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x08);
        writeD(type);
        writeD(members.size());
        members.forEach(member_info -> {
            writeD(member_info.objectId);
            writeS(member_info.name);
            writeD(member_info.classId);
            writeD(member_info.level);
            writeD(member_info.location);
            writeD(member_info.memberType);
            writeD(member_info.instanceReuses.size());
            member_info.instanceReuses.forEach(this::writeD);
        });
    }

    static class PartyRoomMemberInfo {
        final int objectId;
        final int classId;
        final int level;
        final int location;
        final int memberType;
        final String name;
        final Collection<Integer> instanceReuses;

        PartyRoomMemberInfo(Player member, int type) {
            objectId = member.getObjectId();
            name = member.getName();
            classId = member.getClassId().ordinal();
            level = member.getLevel();
            location = MatchingRoomManager.INSTANCE.getLocation(member);
            memberType = type;
            instanceReuses = member.getInstanceReuses().keySet();
        }
    }
}