package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.pledge.Alliance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.model.pledge.UnitMember;

import java.util.List;
import java.util.stream.Collectors;


public class PledgeShowMemberListAll extends L2GameServerPacket {
    private final int _clanObjectId;
    private final int _clanCrestId;
    private final int _level;
    private final int _rank;
    private final int _reputation;
    private final int _hasCastle;
    private final int _hasClanHall;
    private final int _hasFortress;
    private final int _atClanWar;
    private final String _unitName;
    private final String _leaderName;
    private final int _pledgeType;
    private final int _territorySide;
    private final List<PledgePacketMember> _members;
    private int _allianceObjectId;
    private int _allianceCrestId;
    private String _allianceName;

    public PledgeShowMemberListAll(Clan clan, final SubUnit sub) {
        _pledgeType = sub.type();
        _clanObjectId = clan.clanId();
        _unitName = sub.getName();
        _leaderName = sub.getLeaderName();
        _clanCrestId = clan.getCrestId();
        _level = clan.getLevel();
        _hasCastle = clan.getCastle();
        _hasClanHall = clan.getHasHideout();
        _hasFortress = clan.getHasFortress();
        _rank = clan.getRank();
        _reputation = clan.getReputationScore();
        _atClanWar = clan.isAtWarOrUnderAttack();
        _territorySide = clan.getWarDominion();

        Alliance ally = clan.getAlliance();

        if (ally != null) {
            _allianceObjectId = ally.getAllyId();
            _allianceName = ally.getAllyName();
            _allianceCrestId = ally.getAllyCrestId();
        }

        _members = sub.getUnitMembers().stream()
        .map(PledgePacketMember::new)
        .collect(Collectors.toList());

    }

    @Override
    protected final void writeImpl() {
        writeC(0x5a);

        writeD(_pledgeType == Clan.SUBUNIT_MAIN_CLAN ? 0 : 1);
        writeD(_clanObjectId);
        writeD(_pledgeType);
        writeS(_unitName);
        writeS(_leaderName);
        writeD(_clanCrestId); // crest id .. is used again
        writeD(_level);
        writeD(_hasCastle);
        writeD(_hasClanHall);
        writeD(_hasFortress);
        writeD(_rank);
        writeD(_reputation);
        writeD(0x00);
        writeD(0x00);
        writeD(_allianceObjectId);
        writeS(_allianceName);
        writeD(_allianceCrestId);
        writeD(_atClanWar);
        writeD(_territorySide);//territory Id

        writeD(_members.size());
        for (PledgePacketMember m : _members) {
            writeS(m._name);
            writeD(m._level);
            writeD(m._classId);
            writeD(m._sex);
            writeD(m._race);
            writeD(m._online);
            writeD(m._hasSponsor ? 1 : 0);
        }
    }

    private class PledgePacketMember {
        private final String _name;
        private final int _level;
        private final int _classId;
        private final int _sex;
        private final int _race;
        private final int _online;
        private final boolean _hasSponsor;

        PledgePacketMember(UnitMember m) {
            _name = m.getName();
            _level = m.getLevel();
            _classId = m.getClassId();
            _sex = m.getSex();
            _race = 0; //TODO m.race()
            _online = m.isOnline() ? m.objectId() : 0;
            _hasSponsor = m.getSponsor() != 0;
        }
    }
}