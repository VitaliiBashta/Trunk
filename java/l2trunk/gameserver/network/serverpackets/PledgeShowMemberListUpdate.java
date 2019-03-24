package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.model.pledge.UnitMember;

public final class PledgeShowMemberListUpdate extends L2GameServerPacket {
    private final String name;
    private final int lvl;
    private final int classId;
    private final int sex;
    private final int isOnline;
    private final int objectId;
    private final int pledgeType;
    private int isApprentice;

    public PledgeShowMemberListUpdate(final Player player) {
        name = player.getName();
        lvl = player.getLevel();
        classId = player.getClassId().id;
        sex = player.isMale() ? 0 :1;
        objectId = player.objectId();
        isOnline = player.isOnline() ? 1 : 0;
        pledgeType = player.getPledgeType();
        SubUnit subUnit = player.getSubUnit();
        UnitMember member = subUnit == null ? null : subUnit.getUnitMember(objectId);
        if (member != null)
            isApprentice = member.hasSponsor() ? 1 : 0;
    }

    public PledgeShowMemberListUpdate(final UnitMember cm) {
        name = cm.getName();
        lvl = cm.getLevel();
        classId = cm.getClassId();
        sex = cm.getSex();
        objectId = cm.objectId;
        isOnline = cm.isOnline() ? 1 : 0;
        pledgeType = cm.getPledgeType();
        isApprentice = cm.hasSponsor() ? 1 : 0;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x5b);
        writeS(name);
        writeD(lvl);
        writeD(classId);
        writeD(sex);
        writeD(objectId);
        writeD(isOnline); // 1=online 0=offline
        writeD(pledgeType);
        writeD(isApprentice); // does a clan member have a sponsor
    }
}