package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

public final class RequestExMagicSkillUseGround extends L2GameClientPacket {
    private final Location loc = new Location();
    private int skillId;
    private boolean ctrlPressed;
    private boolean shiftPressed;

    /**
     * packet type id 0xd0
     */
    @Override
    protected void readImpl() {
        loc.x = readD();
        loc.y = readD();
        loc.z = readD();
        skillId = readD();
        ctrlPressed = readD() != 0;
        shiftPressed = readC() != 0;
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        Skill skill = SkillTable.INSTANCE.getInfo(skillId, activeChar.getSkillLevel(skillId));
        if (skill != null) {
            if (skill.getAddedSkills().size() == 0)
                return;

            // В режиме трансформации доступны только скилы трансформы
            if (activeChar.getTransformation() != 0 && !activeChar.getAllSkills().contains(skill))
                return;

            if (!activeChar.isInRange(loc, skill.castRange)) {
                activeChar.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
                activeChar.sendActionFailed();
                return;
            }

            Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

            if (skill.checkCondition(activeChar, target, ctrlPressed, shiftPressed, true)) {
                activeChar.setGroundSkillLoc(loc);
                activeChar.getAI().cast(skill, target, ctrlPressed, shiftPressed);
            } else
                activeChar.sendActionFailed();
        } else
            activeChar.sendActionFailed();
    }
}