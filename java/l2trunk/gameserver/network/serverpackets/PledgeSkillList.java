package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public final class PledgeSkillList extends L2GameServerPacket {
    private final List<UnitSkillInfo> _unitSkills = new ArrayList<>();
    private List<SkillInfo> _allSkills;

    public PledgeSkillList(Clan clan) {
        Collection<Skill> skills = clan.getSkills();
        _allSkills = new ArrayList<>(skills.size());

        for (Skill sk : skills)
            _allSkills.add(new SkillInfo(sk.id, sk.level));

        for (SubUnit subUnit : clan.getAllSubUnits()) {
            for (Skill sk : subUnit.getSkills())
                _unitSkills.add(new UnitSkillInfo(subUnit.getType(), sk.id, sk.level));
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3a);
        writeD(_allSkills.size());
        writeD(_unitSkills.size());

        for (SkillInfo info : _allSkills) {
            writeD(info._id);
            writeD(info._level);
        }

        for (UnitSkillInfo info : _unitSkills) {
            writeD(info._type);
            writeD(info._id);
            writeD(info._level);
        }
    }

    static class SkillInfo {
        final int _id;
        final int _level;

        SkillInfo(int id, int level) {
            _id = id;
            _level = level;
        }
    }

    static class UnitSkillInfo extends SkillInfo {
        private final int _type;

        UnitSkillInfo(int type, int id, int level) {
            super(id, level);
            _type = type;
        }
    }
}