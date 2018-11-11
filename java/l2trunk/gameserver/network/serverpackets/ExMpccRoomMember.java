package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.matching.MatchingRoom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 */
public class ExMpccRoomMember extends L2GameServerPacket {
    private final int _type;
    private List<MpccRoomMemberInfo> _members = Collections.emptyList();

    public ExMpccRoomMember(MatchingRoom room, Player player) {
        _type = room.getMemberType(player);
        _members = new ArrayList<>(room.getPlayers().size());

        for (Player member : room.getPlayers())
            _members.add(new MpccRoomMemberInfo(member, room.getMemberType(member)));
    }

    @Override
    public void writeImpl() {
        writeEx(0x9F);
        writeD(_type);
        writeD(_members.size());
        for (MpccRoomMemberInfo member : _members) {
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
            this.objectId = member.getObjectId();
            this.name = member.getName();
            this.classId = member.getClassId().ordinal();
            this.level = member.getLevel();
            this.location = MatchingRoomManager.getInstance().getLocation(member);
            this.memberType = type;
        }
    }
}