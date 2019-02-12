package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Party;

import java.util.List;
import java.util.stream.Collectors;


public final class ExMPCCShowPartyMemberInfo extends L2GameServerPacket {
    private final List<PartyMemberInfo> members;

    public ExMPCCShowPartyMemberInfo(Party party) {
        members =  party.getMembers().stream()
        .map(m -> new PartyMemberInfo(m.getName(), m.objectId(), m.getClassId().id))
        .collect(Collectors.toList());
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x4b);
        writeD(members.size()); // Количество членов в пати

        for (PartyMemberInfo member : members) {
            writeS(member.name); // Имя члена пати
            writeD(member.objectId); // object Id члена пати
            writeD(member.classId); // id класса члена пати
        }

        members.clear();
    }

    private static class PartyMemberInfo {
        final String name;
        final int objectId;
        final int classId;

        PartyMemberInfo(String name, int objectId, int classId) {
            this.name = name;
            this.objectId = objectId;
            this.classId = classId;
        }
    }
}