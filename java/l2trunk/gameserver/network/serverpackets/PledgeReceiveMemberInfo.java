package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.UnitMember;

public class PledgeReceiveMemberInfo extends L2GameServerPacket {
    private final UnitMember member;

    public PledgeReceiveMemberInfo(UnitMember member) {
        this.member = member;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3e);

        writeD(member.getPledgeType());
        writeS(member.getName());
        writeS(member.getTitle());
        writeD(member.getPowerGrade());
        writeS(member.getSubUnit().getName());
        writeS(member.getRelatedName()); // apprentice/sponsor name if any
    }
}