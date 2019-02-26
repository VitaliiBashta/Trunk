package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

import java.util.ArrayList;
import java.util.List;

public final class ExMpccRoomMember extends L2GameServerPacket {
    private final int type;
    private List<MpccRoomMemberInfo> members = new ArrayList<>();

    public ExMpccRoomMember(MatchingRoom room, Player player) {
        type = room.getMemberType(player);
        room.getPlayers().forEach(member -> members.add(new MpccRoomMemberInfo(member, room.getMemberType(member))));
    }

    @Override
    public void writeImpl() {
        writeEx(0x9F);
        writeD(type);
        writeD(members.size());
        for (MpccRoomMemberInfo member : members) {
            writeD(member.objectId);
            writeS(member.name);
            writeD(member.level);
            writeD(member.classId);
            writeD(member.location);
            writeD(member.memberType);
        }
    }

    static class MpccRoomMemberInfo {
        final int objectId;
        final int classId;
        final int level;
        final int location;
        final int memberType;
        final String name;

        MpccRoomMemberInfo(Player member, int type) {
            this.objectId = member.objectId();
            this.name = member.getName();
            this.classId = member.getClassId().id;
            this.level = member.getLevel();
            this.location = MatchingRoomManager.INSTANCE.getLocation(member);
            this.memberType = type;
        }
    }
}