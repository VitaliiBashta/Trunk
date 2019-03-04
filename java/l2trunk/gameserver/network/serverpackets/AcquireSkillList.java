package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.base.AcquireType;

import java.util.ArrayList;
import java.util.List;


public final class AcquireSkillList extends L2GameServerPacket {
    private final List<AcruireSkill> skills;
    private final AcquireType type;

    public AcquireSkillList(AcquireType type, int size) {
        skills = new ArrayList<>(size);
        this.type = type;
    }

    public void addSkill(int id, int nextLevel, int maxLevel, int cost, int requirements, int subUnit) {
        skills.add(new AcruireSkill(id, nextLevel, maxLevel, cost, requirements, subUnit));
    }

    public void addSkill(int id, int nextLevel, int maxLevel, int cost) {
        skills.add(new AcruireSkill(id, nextLevel, maxLevel, cost, 0, 0));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x90);
        writeD(type.ordinal());
        writeD(skills.size());

        skills.forEach(skill -> {
            writeD(skill.id);
            writeD(skill.nextLevel);
            writeD(skill.maxLevel);
            writeD(skill.cost);
            writeD(skill.requirements);
            if (type == AcquireType.SUB_UNIT)
                writeD(skill.subUnit);
        });
    }

    class AcruireSkill {
        final int id;
        final int nextLevel;
        final int maxLevel;
        final int cost;
        final int requirements;
        final int subUnit;

        AcruireSkill(int id, int nextLevel, int maxLevel, int cost, int requirements, int subUnit) {
            this.id = id;
            this.nextLevel = nextLevel;
            this.maxLevel = maxLevel;
            this.cost = cost;
            this.requirements = requirements;
            this.subUnit = subUnit;
        }
    }
}