package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.EnchantSkillLearn;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTreeTable;
import l2trunk.gameserver.utils.Log;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

/**
 * Format (ch) dd
 */
public final class RequestExEnchantSkillSafe extends L2GameClientPacket {
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

        if (activeChar.isTrasformed()) {
            activeChar.sendMessage("You must leave transformation mode first.");
            return;
        }

        if (activeChar.getLevel() < 76 || activeChar.getClassId().occupation() < 3) {
            activeChar.sendMessage("You must have 3rd class change quest completed.");
            return;
        }

        EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(_skillId, _skillLvl);

        if (sl == null)
            return;

        int slevel = activeChar.getSkillLevel(_skillId);
        if (slevel == -1)
            return;

        int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), _skillLvl, sl.getMaxLevel());

        // already knows the skill with this occupation
        if (slevel >= enchantLevel)
            return;

        // Можем ли мы перейти с текущего уровня скилла на данную заточку
        if (slevel == sl.getBaseLevel() ? _skillLvl % 100 != 1 : slevel != enchantLevel - 1) {
            activeChar.sendMessage("Incorrect enchant occupation.");
            return;
        }

        int[] cost = sl.getCost();
        int requiredSp = cost[1] * SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER * sl.getCostMult();
        int requiredAdena = cost[0] * SkillTreeTable.SAFE_ENCHANT_COST_MULTIPLIER * sl.getCostMult();

        int rate = sl.getRate(activeChar);

        if (activeChar.getSp() < requiredSp) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        if (!activeChar.haveAdena(requiredAdena)) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (!activeChar.haveItem(SkillTreeTable.SAFE_ENCHANT_BOOK) ) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
            return;
        }

        removeItem(activeChar, SkillTreeTable.SAFE_ENCHANT_BOOK, 1, "SkillEnchantSafe");

        if (Rnd.chance(rate)) {
            activeChar.addSkill(_skillId,enchantLevel, true);
            activeChar.addExpAndSp(0, -1 * requiredSp);
            removeItem(activeChar, 57, requiredAdena, "SkillEnchantSafe");
            activeChar.sendPacket(new SystemMessage2(SystemMsg.YOUR_SP_HAS_DECREASED_BY_S1).addInteger(requiredSp), new SystemMessage2(SystemMsg.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(1));
            activeChar.sendPacket(new SkillList(activeChar));
            RequestExEnchantSkill.updateSkillShortcuts(activeChar, _skillId, _skillLvl);
            Log.add(activeChar.getName() + "|Successfully safe enchanted|" + _skillId + "|to+" + _skillLvl + "|" + rate, "enchant_skills");
        } else {
            activeChar.sendPacket(new SystemMessage(SystemMessage.Skill_enchant_failed_Current_level_of_enchant_skill_S1_will_remain_unchanged).addSkillName(_skillId, _skillLvl), new ExEnchantSkillResult(0));
            Log.add(activeChar.getName() + "|Failed to safe enchant|" + _skillId + "|to+" + _skillLvl + "|" + rate, "enchant_skills");
        }

        activeChar.sendPacket(new ExEnchantSkillInfo(_skillId, activeChar.getSkillDisplayLevel(_skillId)));
    }
}