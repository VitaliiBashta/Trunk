package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.base.AcquireType;

import java.util.ArrayList;
import java.util.List;

public final class AcquireSkillList extends L2GameServerPacket {
    private final List<SkillDescription> skills;
    private final AcquireType type;

    public AcquireSkillList(AcquireType type, int size) {
        skills = new ArrayList<>(size);
        this.type = type;
    }

    public void addSkill(SkillLearn skill) {
        skills.add(new SkillDescription(skill, 0, 0));
    }

    public void addSkill(SkillLearn skill, int requirements, int subUnit) {
        skills.add(new SkillDescription(skill, requirements, subUnit));
    }
//    public void addSkill(int id, int nextLevel, int maxLevel, int cost, int requirements, int subUnit) {
//        skills.add(new SkillDescription(id, nextLevel, maxLevel, cost, requirements, subUnit));
//    }

//    public void addSkill(int id, int nextLevel, int maxLevel, int cost, int requirements) {
//        skills.add(new SkillDescription(id, nextLevel, maxLevel, cost, requirements, 0));
//    }

    @Override
    protected final void writeImpl() {
        writeC(0x90);
        writeD(type.ordinal());
        writeD(skills.size());

        skills.forEach(s -> {
            writeD(s.id);
            writeD(s.nextLevel);
            writeD(s.maxLevel);
            writeD(s.cost);
            writeD(s.requirements);
            if (type == AcquireType.SUB_UNIT)
                writeD(s.subUnit);
        });
    }

    class SkillDescription {
        final int id;
        final int nextLevel;
        final int maxLevel;
        final int cost;
        final int requirements;
        final int subUnit;

        SkillDescription(SkillLearn skill, int requirements, int subUnit) {
            this.id = skill.getId();
            this.nextLevel = skill.getLevel();
            this.maxLevel = skill.getLevel();
            this.cost = skill.getCost();
            this.requirements = requirements;
            this.subUnit = subUnit;
        }
    }
}