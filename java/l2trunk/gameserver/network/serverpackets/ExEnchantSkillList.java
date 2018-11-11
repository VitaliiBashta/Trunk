package l2trunk.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

public class ExEnchantSkillList extends L2GameServerPacket {
    private final List<Skill> _skills;
    private final EnchantSkillType _type;

    public ExEnchantSkillList(EnchantSkillType type) {
        _type = type;
        _skills = new ArrayList<>();
    }

    public void addSkill(int id, int level) {
        _skills.add(new Skill(id, level));
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x29);

        writeD(_type.ordinal());
        writeD(_skills.size());
        for (Skill sk : _skills) {
            writeD(sk.id);
            writeD(sk.level);
        }
    }

    public enum EnchantSkillType {
        NORMAL,
        SAFE,
        UNTRAIN,
        CHANGE_ROUTE,
    }

    class Skill {
        final int id;
        final int level;

        Skill(int id, int nextLevel) {
            this.id = id;
            level = nextLevel;
        }
    }
}