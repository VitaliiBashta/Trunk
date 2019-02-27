package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTreeTable;

import java.util.ArrayList;
import java.util.List;


/**
 * format   d (dddc)
 */
public final class SkillList extends L2GameServerPacket {
    private final List<Skill> skills;
    private final boolean canEnchant;
    private final Player activeChar;

    public SkillList(Player p) {
        skills = new ArrayList<>(p.getAllSkills());
        canEnchant = !p.isTrasformed();
        activeChar = p;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x5f);
        writeD(skills.size());

        skills.forEach(skill -> {
            writeD(skill.isActive() || skill.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
            writeD(skill.getDisplayLevel());
            writeD(skill.displayId);
            writeC(activeChar.isUnActiveSkill(skill.id) ? 0x01 : 0x00); // иконка скилла серая если не 0
            writeC(canEnchant ? SkillTreeTable.isEnchantable(skill) : 0); // для заточки: если 1 скилл можно точить
        });
    }
}