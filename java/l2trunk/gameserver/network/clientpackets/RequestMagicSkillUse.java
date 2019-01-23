package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.tables.SkillTable;

public final class RequestMagicSkillUse extends L2GameClientPacket {
    private Integer magicId;
    private boolean ctrlPressed;
    private boolean shiftPressed;

    @Override
    protected void readImpl() {
        magicId = readD();
        ctrlPressed = readD() != 0;
        shiftPressed = readC() != 0;
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();

        if (activeChar == null) {
            getClient().sendPacket(ActionFail.STATIC);
            return;
        }

        activeChar.setActive();

        if (activeChar.isOutOfControl()) {
            activeChar.setMacroSkill(null);
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.getMacroSkill() != null) {
            this.magicId = activeChar.getMacroSkill().id;
        }
        Skill skill = SkillTable.INSTANCE.getInfo(this.magicId, activeChar.getSkillLevel(this.magicId));

        if (activeChar.isPendingOlyEnd()) {
            if ((skill != null) && (skill.isOffensive)) {
                activeChar.setMacroSkill(null);
                activeChar.sendActionFailed();
                return;
            }
        }
        if (skill != null) {
            if ((!skill.isActive()) && (!skill.isToggle())) {
                activeChar.setMacroSkill(null);
                return;
            }

            FlagItemAttachment attachment = activeChar.getActiveWeaponFlagAttachment();
            if ((attachment != null) && (!attachment.canCast(activeChar, skill))) {
                activeChar.setMacroSkill(null);
                activeChar.sendActionFailed();
                return;
            }

            if ((activeChar.getTransformation() != 0) && (!activeChar.getAllSkills().contains(skill))) {
                activeChar.setMacroSkill(null);
                return;
            }

            if ((skill.isToggle()) && (activeChar.getEffectList().getEffectsBySkill(skill) != null)) {
                activeChar.setMacroSkill(null);
                activeChar.getEffectList().stopEffect(skill.id);
                activeChar.sendActionFailed();
                return;
            }

            Creature target = skill.getAimingTarget(activeChar, activeChar.getTarget());

//			if ((target == null) || target.isDead())
//			{
//				activeChar.sendPacket(SystemMsg.INVALID_TARGET);
//				return;
//			}

            activeChar.setGroundSkillLoc(null);

            if (activeChar.getMacroSkill() != null) {
                if (skill.getReuseDelay(activeChar) < 9000L) {
                    activeChar.setReuseDelay(skill.getReuseDelay(activeChar) - 3000L);
                    activeChar.setMacroSkill(null);
                }
            }
//			if (!activeChar.isCastingNow() && !skill.isToggle() && skill.isOffensive())
//			{
//				activeChar.broadcastPacket(new MoveToPawn(activeChar, target, (int) activeChar.getDistance(target)));
//			}
            activeChar.getAI().Cast(skill, target, ctrlPressed, shiftPressed);
        } else {
            activeChar.setMacroSkill(null);
            activeChar.sendActionFailed();
        }
    }
}