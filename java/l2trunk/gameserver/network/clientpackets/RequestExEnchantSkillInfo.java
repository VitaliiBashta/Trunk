package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.base.EnchantSkillLearn;
import l2trunk.gameserver.network.serverpackets.ExEnchantSkillInfo;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.tables.SkillTreeTable;

public final class RequestExEnchantSkillInfo extends L2GameClientPacket {
    private int skillId;
    private int skillLvl;

    @Override
    protected void readImpl() {
        skillId = readD();
        skillLvl = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (skillLvl > 100) {
            EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(skillId, skillLvl);
            if (sl == null) {
                activeChar.sendMessage("Not found enchant info for this skill");
                return;
            }

            Skill skill = SkillTable.INSTANCE.getInfo(skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), skillLvl, sl.getMaxLevel()));

            if (skill == null || skill.id != skillId) {
                activeChar.sendMessage("This skill doesn't yet have enchant info in Datapack");
                return;
            }

            if (activeChar.getSkillLevel(skillId) != skill.level) {
                activeChar.sendMessage("Skill not found");
                return;
            }
        } else if (activeChar.getSkillLevel(skillId) != skillLvl) {
            activeChar.sendMessage("Skill not found");
            return;
        }

        sendPacket(new ExEnchantSkillInfo(skillId, skillLvl));
    }
}