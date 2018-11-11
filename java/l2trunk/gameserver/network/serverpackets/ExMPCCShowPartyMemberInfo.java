package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;


/**
 * Format: ch d[Sdd]
 *
 * @author SYS
 */
public class ExMPCCShowPartyMemberInfo extends L2GameServerPacket {
    private final List<PartyMemberInfo> members;

    public ExMPCCShowPartyMemberInfo(Party party) {
        members = new ArrayList<>();
        for (Player _member : party.getMembers())
            members.add(new PartyMemberInfo(_member.getName(), _member.getObjectId(), _member.getClassId().getId()));
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x4b);
        writeD(members.size()); // Количество членов в пати

        for (PartyMemberInfo member : members) {
            writeS(member.name); // Имя члена пати
            writeD(member.object_id); // object Id члена пати
            writeD(member.class_id); // id класса члена пати
        }

        members.clear();
    }

    static class PartyMemberInfo {
        final String name;
        final int object_id;
        final int class_id;

        PartyMemberInfo(String _name, int _object_id, int _class_id) {
            name = _name;
            object_id = _object_id;
            class_id = _class_id;
        }
    }
}