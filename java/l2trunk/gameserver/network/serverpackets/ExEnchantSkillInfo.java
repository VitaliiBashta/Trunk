package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.base.EnchantSkillLearn;
import l2trunk.gameserver.tables.SkillTreeTable;

import java.util.ArrayList;
import java.util.List;


public class ExEnchantSkillInfo extends L2GameServerPacket {
    private final List<Integer> _routes;

    private final int _id;
    private final int _level;
    private int _canAdd;
    private int canDecrease;

    public ExEnchantSkillInfo(int id, int level) {
        _routes = new ArrayList<>();
        _id = id;
        _level = level;

        // skill already enchanted?
        if (_level > 100) {
            canDecrease = 1;
            // get detail for next occupation
            EnchantSkillLearn esd = SkillTreeTable.getSkillEnchant(_id, _level + 1);

            // if it exists add it
            if (esd != null) {
                addEnchantSkillDetail(esd.getLevel());
                _canAdd = 1;
            }

            for (EnchantSkillLearn el : SkillTreeTable.getEnchantsForChange(_id, _level))
                addEnchantSkillDetail(el.getLevel());
        } else
            // not already enchanted
            for (EnchantSkillLearn esd : SkillTreeTable.getFirstEnchantsForSkill(_id)) {
                addEnchantSkillDetail(esd.getLevel());
                _canAdd = 1;
            }
    }

    private void addEnchantSkillDetail(int level) {
        _routes.add(level);
    }

    @Override
    protected void writeImpl() {
        writeEx(0x2a);

        writeD(_id);
        writeD(_level);
        writeD(_canAdd); // can add enchant
        writeD(canDecrease); // can decrease enchant

        writeD(_routes.size());
        for (Integer route : _routes)
            writeD(route);
    }
}