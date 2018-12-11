package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.base.EnchantSkillLearn;
import l2trunk.gameserver.network.serverpackets.ExEnchantSkillInfo;
import l2trunk.gameserver.network.serverpackets.ExEnchantSkillResult;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.tables.SkillTreeTable;
import l2trunk.gameserver.utils.Log;

public final class RequestExEnchantSkillUntrain extends L2GameClientPacket {
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

        if (activeChar.getTransformation() != 0) {
            activeChar.sendMessage("You must leave transformation mode first.");
            return;
        }

        if (activeChar.getLevel() < 76 || activeChar.getClassId().getLevel() < 4) {
            activeChar.sendMessage("You must have 3rd class change quest completed.");
            return;
        }

        int oldSkillLevel = activeChar.getSkillDisplayLevel(skillId);
        if (oldSkillLevel == -1)
            return;

        if (skillLvl != (oldSkillLevel - 1) || (skillLvl / 100) != (oldSkillLevel / 100))
            return;

        EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(skillId, oldSkillLevel);
        if (sl == null)
            return;

        Skill newSkill;

        if (skillLvl % 100 == 0) {
            skillLvl = sl.getBaseLevel();
            newSkill = SkillTable.INSTANCE.getInfo(skillId, skillLvl);
        } else
            newSkill = SkillTable.INSTANCE.getInfo(skillId, SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), skillLvl, sl.getMaxLevel()));

        if (newSkill == null)
            return;

        if (Functions.getItemCount(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK) == 0) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
            return;
        }

        Functions.removeItem(activeChar, SkillTreeTable.UNTRAIN_ENCHANT_BOOK, 1, "SkillEnchantUntrain");

        activeChar.addExpAndSp(0, sl.getCost()[1] * sl.getCostMult());
        activeChar.addSkill(newSkill, true);

        if (skillLvl > 100) {
            SystemMessage sm = new SystemMessage(SystemMessage.Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_has_been_decreased_by_1);
            sm.addSkillName(skillId, skillLvl);
            activeChar.sendPacket(sm);
        } else {
            SystemMessage sm = new SystemMessage(SystemMessage.Untrain_of_enchant_skill_was_successful_Current_level_of_enchant_skill_S1_became_0_and_enchant_skill_will_be_initialized);
            sm.addSkillName(skillId, skillLvl);
            activeChar.sendPacket(sm);
        }

        Log.add(activeChar.getName() + "|Successfully untranes|" + skillId + "|to+" + skillLvl + "|---", "enchant_skills");

        activeChar.sendPacket(new ExEnchantSkillInfo(skillId, newSkill.getDisplayLevel()), ExEnchantSkillResult.SUCCESS, new SkillList(activeChar));
        RequestExEnchantSkill.updateSkillShortcuts(activeChar, skillId, skillLvl);
    }
}