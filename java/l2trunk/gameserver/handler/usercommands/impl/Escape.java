package l2trunk.gameserver.handler.usercommands.impl;

import l2trunk.gameserver.handler.usercommands.IUserCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Collections;
import java.util.List;

/**
 * Support for /unstuck command
 */
public final class Escape implements IUserCommandHandler {
    private static final Integer COMMAND_IDS = 52;

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_IDS !=id)
            return false;

        if (activeChar.isMovementDisabled() || activeChar.isOutOfControl() || activeChar.isInOlympiadMode())
            return false;

        if (activeChar.getTeleMode() != 0 || !activeChar.getPlayerAccess().UseTeleport) {
            activeChar.sendMessage(new CustomMessage("common.TryLater", activeChar));
            return false;
        }
        if (activeChar.isJailed()) {
            activeChar.sendMessage("You cannot escape from Jail!");
            return false;
        }

        if (activeChar.isTerritoryFlagEquipped()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
            return false;
        }

        if (activeChar.isInDuel() || activeChar.getTeam() != TeamType.NONE) {
            activeChar.sendMessage(new CustomMessage("common.RecallInDuel", activeChar));
            return false;
        }

        activeChar.abortAttack(true, true);
        activeChar.abortCast(true, true);
        activeChar.stopMove();

        Skill skill;
        if (activeChar.getPlayerAccess().FastUnstuck)
            skill = SkillTable.INSTANCE().getInfo(1050, 2);
        else
            skill = SkillTable.INSTANCE().getInfo(2099, 1);

        if (skill != null && skill.checkCondition(activeChar, activeChar, false, false, true))
            activeChar.getAI().Cast(skill, activeChar, false, true);

        return true;
    }

    @Override
    public final List<Integer> getUserCommandList() {
        return Collections.singletonList(COMMAND_IDS);
    }
}