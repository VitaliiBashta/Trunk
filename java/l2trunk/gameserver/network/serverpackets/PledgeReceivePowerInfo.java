package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.RankPrivs;
import l2trunk.gameserver.model.pledge.UnitMember;

public class PledgeReceivePowerInfo extends L2GameServerPacket {
    private final int PowerGrade;
    private final int privs;
    private final String member_name;

    public PledgeReceivePowerInfo(UnitMember member) {
        PowerGrade = member.getPowerGrade();
        member_name = member.getName();
        if (member.isClanLeader())
            privs = Clan.CP_ALL;
        else {
            RankPrivs temp = member.getClan().getRankPrivs(member.getPowerGrade());
            if (temp != null)
                privs = temp.getPrivs();
            else
                privs = 0;
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3d);
        writeD(PowerGrade);
        writeS(member_name);
        writeD(privs);
    }
}