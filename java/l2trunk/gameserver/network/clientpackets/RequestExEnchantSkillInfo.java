package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.base.EnchantSkillLearn;
import l2trunk.gameserver.network.serverpackets.ExEnchantSkillInfo;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.tables.SkillTreeTable;

public class RequestExEnchantSkillInfo extends L2GameClientPacket {
    private int _skillId;
    private int _skillLvl;

    @Override
    protected void readImpl() {
        _skillId = readD();
        _skillLvl = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (_skillLvl > 100) {
            EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);
            if (sl == null) {
                activeChar.sendMessage("Not found enchant info for this skill");
                return;
            }

            Skill skill = SkillTable.INSTANCE.getInfo(_skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel()));

            if (skill == null || skill.id != _skillId) {
                activeChar.sendMessage("This skill doesn't yet have enchant info in Datapack");
                return;
            }

            if (activeChar.getSkillLevel(_skillId) != skill.level) {
                activeChar.sendMessage("Skill not found");
                return;
            }
        } else if (activeChar.getSkillLevel(_skillId) != _skillLvl) {
            activeChar.sendMessage("Skill not found");
            return;
        }

        sendPacket(new ExEnchantSkillInfo(_skillId, _skillLvl));
    }
}