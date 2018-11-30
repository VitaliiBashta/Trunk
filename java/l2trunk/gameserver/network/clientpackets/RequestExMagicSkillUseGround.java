package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

/**
 * @author SYS
 * @date 08/9/2007
 * Format: chdddddc
 * <p>
 * Пример пакета:
 * D0
 * 2F 00
 * E4 35 00 00 x
 * 62 D1 02 00 y
 * 22 F2 FF FF z
 * 90 05 00 00 skill id
 * 00 00 00 00 ctrlPressed
 * 00 shiftPressed
 */
public class RequestExMagicSkillUseGround extends L2GameClientPacket {
    private final Location _loc = new Location();
    private int _skillId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    /**
     * packet type id 0xd0
     */
    @Override
    protected void readImpl() {
        _loc.x = readD();
        _loc.y = readD();
        _loc.z = readD();
        _skillId = readD();
        _ctrlPressed = readD() != 0;
        _shiftPressed = readC() != 0;
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

        Skill skill = SkillTable.INSTANCE().getInfo(_skillId, activeChar.getSkillLevel(_skillId));
        if (skill != null) {
            if (skill.getAddedSkills().size() == 0)
                return;

            // В режиме трансформации доступны только скилы трансформы
            if (activeChar.getTransformation() != 0 && !activeChar.getAllSkills().contains(skill))
                return;

            if (!activeChar.isInRange(_loc, skill.getCastRange())) {
                activeChar.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
                activeChar.sendActionFailed();
                return;
            }

            Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

            if (skill.checkCondition(activeChar, target, _ctrlPressed, _shiftPressed, true)) {
                activeChar.setGroundSkillLoc(_loc);
                activeChar.getAI().Cast(skill, target, _ctrlPressed, _shiftPressed);
            } else
                activeChar.sendActionFailed();
        } else
            activeChar.sendActionFailed();
    }
}