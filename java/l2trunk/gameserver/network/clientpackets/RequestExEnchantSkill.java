package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.actor.instances.player.ShortCut;
import l2trunk.gameserver.model.base.EnchantSkillLearn;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.tables.SkillTreeTable;
import l2trunk.gameserver.utils.Log;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class RequestExEnchantSkill extends L2GameClientPacket {
    private int skillId;
    private int skillLvl;

    static void updateSkillShortcuts(Player player, int skillId, int skillLevel) {
        for (ShortCut sc : player.getAllShortCuts())
            if (sc.getId() == skillId && sc.getType() == ShortCut.TYPE_SKILL) {
                ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, 1);
                player.sendPacket(new ShortCutRegister(player, newsc));
                player.registerShortCut(newsc);
            }
    }

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

        if (activeChar.isBusy()) {
            return;
        }

        if ((activeChar.getTransformation() != 0) || (activeChar.isMounted()) || (Olympiad.isRegisteredInComp(activeChar)) || (activeChar.isInCombat())) {
            activeChar.sendMessage("You must leave transformation mode first.");
            return;
        }

        if (activeChar.getLevel() < 76 || activeChar.getClassId().occupation() < 3) {
            activeChar.sendMessage("You must have 3rd class change quest completed.");
            return;
        }

        EnchantSkillLearn sl = SkillTreeTable.getSkillEnchant(skillId, skillLvl);
        if (sl == null)
            return;

        int slevel = activeChar.getSkillLevel(skillId);
        if (slevel == -1)
            return;

        int enchantLevel = SkillTreeTable.convertEnchantLevel(sl.getBaseLevel(), skillLvl, sl.getMaxLevel());

        // already knows the skill with this occupation
        if (slevel >= enchantLevel)
            return;

        // Можем ли мы перейти с текущего уровня скилла на данную заточку
        if (slevel == sl.getBaseLevel() ? skillLvl % 100 != 1 : slevel != enchantLevel - 1) {
            activeChar.sendMessage("Incorrect enchant occupation.");
            return;
        }

        Skill skill = SkillTable.INSTANCE.getInfo(skillId, enchantLevel);
        if (skill == null) {
            activeChar.sendMessage("Internal error: not found skill occupation");
            return;
        }

        int[] cost = sl.getCost();
        int requiredSp = cost[1] * SkillTreeTable.NORMAL_ENCHANT_COST_MULTIPLIER * sl.getCostMult();
        int requiredAdena = cost[0] * SkillTreeTable.NORMAL_ENCHANT_COST_MULTIPLIER * sl.getCostMult();
        int rate = sl.getRate(activeChar);

        if (activeChar.getSp() < requiredSp) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL);
            return;
        }

        if (activeChar.getAdena() < requiredAdena) {
            activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        if (skillLvl % 100 == 1) // only first lvl requires book (101, 201, 301 ...)
        {
            if (!activeChar.haveItem(SkillTreeTable.NORMAL_ENCHANT_BOOK)) {
                activeChar.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL);
                return;
            }
            removeItem(activeChar, SkillTreeTable.NORMAL_ENCHANT_BOOK, 1, "SkillEnchant");
        }

        if (Rnd.chance(rate)) {
            activeChar.addExpAndSp(0, -1 * requiredSp);
            removeItem(activeChar, 57, requiredAdena, "SkillEnchant");
            activeChar.sendPacket(new SystemMessage2(SystemMsg.YOUR_SP_HAS_DECREASED_BY_S1).addInteger(requiredSp), new SystemMessage2(SystemMsg.SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED).addSkillName(skillId, skillLvl), new SkillList(activeChar), new ExEnchantSkillResult(1));
            Log.add(activeChar.getName() + "|Successfully enchanted|" + skillId + "|to+" + skillLvl + "|" + rate, "enchant_skills");
        } else {
            skill = SkillTable.INSTANCE.getInfo(skillId, sl.getBaseLevel());
            activeChar.sendPacket(new SystemMessage(SystemMessage.FAILED_IN_ENCHANTING_SKILL_S1).addSkillName(skillId, skillLvl), new ExEnchantSkillResult(0));
            Log.add(activeChar.getName() + "|Failed to enchant|" + skillId + "|to+" + skillLvl + "|" + rate, "enchant_skills");
        }
        activeChar.addSkill(skill, true);
        updateSkillShortcuts(activeChar, skillId, skillLvl);
        activeChar.sendPacket(new ExEnchantSkillInfo(skillId, activeChar.getSkillDisplayLevel(skillId)));
    }
}