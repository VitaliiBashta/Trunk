package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

public final class ExManageMpccRoomMember extends L2GameServerPacket {
    public static final int ADD_MEMBER = 0;
    public static final int UPDATE_MEMBER = 1;
    public static final int REMOVE_MEMBER = 2;

    private final int type;
    private final MpccRoomMemberInfo memberinfo;

    public ExManageMpccRoomMember(int type, MatchingRoom room, Player target) {
        this.type = type;
        memberinfo = (new MpccRoomMemberInfo(target, room.getMemberType(target)));
    }

    @Override
    protected void writeImpl() {
        writeEx(0x9E);
        writeD(type);
        writeD(memberinfo.objectId);
        writeS(memberinfo.name);
        writeD(memberinfo.level);
        writeD(memberinfo.classId);
        writeD(memberinfo.location);
        writeD(memberinfo.memberType);
    }

    static class MpccRoomMemberInfo {
        final int objectId;
        final int classId;
        final int level;
        final int location;
        final int memberType;
        final String name;

        MpccRoomMemberInfo(Player member, int type) {
            this.objectId = member.getObjectId();
            this.name = member.getName();
            this.classId = member.getClassId().ordinal();
            this.level = member.getLevel();
            this.location = MatchingRoomManager.INSTANCE.getLocation(member);
            this.memberType = type;
        }
    }
}