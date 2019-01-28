package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public final class PledgeSkillList extends L2GameServerPacket {
    private final List<UnitSkillInfo> unitSkills = new ArrayList<>();
    private List<SkillInfo> allSkills;

    public PledgeSkillList(Clan clan) {
        Collection<Skill> skills = clan.getSkills();
        allSkills = new ArrayList<>(skills.size());

        for (Skill sk : skills)
            allSkills.add(new SkillInfo(sk.id, sk.level));

        for (SubUnit subUnit : clan.getAllSubUnits()) {
            for (Skill sk : subUnit.getSkills())
                unitSkills.add(new UnitSkillInfo(subUnit.getType(), sk.id, sk.level));
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x3a);
        writeD(allSkills.size());
        writeD(unitSkills.size());

        allSkills.forEach(info -> {
            writeD(info.id);
            writeD(info.level);
        });

        unitSkills.forEach(info -> {
            writeD(info.type);
            writeD(info.id);
            writeD(info.level);
        });
    }

    private static class SkillInfo {
        final int id;
        final int level;

        SkillInfo(int id, int level) {
            this.id = id;
            this.level = level;
        }
    }

    private static class UnitSkillInfo extends SkillInfo {
        private final int type;

        UnitSkillInfo(int type, int id, int level) {
            super(id, level);
            this.type = type;
        }
    }
}