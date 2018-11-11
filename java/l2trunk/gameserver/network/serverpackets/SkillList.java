package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTreeTable;

import java.util.ArrayList;
import java.util.List;


/**
 * format   d (dddc)
 */
public class SkillList extends L2GameServerPacket {
    private final List<Skill> _skills;
    private final boolean canEnchant;
    private final Player activeChar;

    public SkillList(Player p) {
        _skills = new ArrayList<>(p.getAllSkills());
        canEnchant = p.getTransformation() == 0;
        activeChar = p;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x5f);
        writeD(_skills.size());

        for (Skill temp : _skills) {
            writeD(temp.isActive() || temp.isToggle() ? 0 : 1); // deprecated? клиентом игнорируется
            writeD(temp.getDisplayLevel());
            writeD(temp.getDisplayId());
            writeC(activeChar.isUnActiveSkill(temp.getId()) ? 0x01 : 0x00); // иконка скилла серая если не 0
            writeC(canEnchant ? SkillTreeTable.isEnchantable(temp) : 0); // для заточки: если 1 скилл можно точить
        }
    }
}