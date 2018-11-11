package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

/**
 * @author VISTALL
 */
public class ExManageMpccRoomMember extends L2GameServerPacket {
    public static final int ADD_MEMBER = 0;
    public static final int UPDATE_MEMBER = 1;
    public static final int REMOVE_MEMBER = 2;

    private final int _type;
    private final MpccRoomMemberInfo _memberInfo;

    public ExManageMpccRoomMember(int type, MatchingRoom room, Player target) {
        _type = type;
        _memberInfo = (new MpccRoomMemberInfo(target, room.getMemberType(target)));
    }

    @Override
    protected void writeImpl() {
        writeEx(0x9E);
        writeD(_type);
        writeD(_memberInfo.objectId);
        writeS(_memberInfo.name);
        writeD(_memberInfo.level);
        writeD(_memberInfo.classId);
        writeD(_memberInfo.location);
        writeD(_memberInfo.memberType);
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
            this.location = MatchingRoomManager.getInstance().getLocation(member);
            this.memberType = type;
        }
    }
}