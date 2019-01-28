package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Collection;

public final class GMViewSkillInfo extends L2GameServerPacket {
    private final String charName;
    private final Collection<Skill> skills;
    private final Player targetChar;

    public GMViewSkillInfo(Player cha) {
        charName = cha.getName();
        skills = cha.getAllSkills();
        targetChar = cha;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x97);
        writeS(charName);
        writeD(skills.size());
        skills.forEach(skill -> {
            writeD(skill.isPassive() ? 1 : 0);
            writeD(skill.getDisplayLevel());
            writeD(skill.id);
            writeC(targetChar.isUnActiveSkill(skill.id) ? 0x01 : 0x00);
            writeC(SkillTable.INSTANCE.getMaxLevel(skill.id) > 100 ? 1 : 0);
        });
    }
}