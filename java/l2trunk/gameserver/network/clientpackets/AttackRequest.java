package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;

@SuppressWarnings("unused")
public class AttackRequest extends L2GameClientPacket {
    private int _objectId;
    private int _originX;
    private int _originY;
    private int _originZ;
    private int _attackId;

    protected void readImpl() {
        _objectId = readD();
        _originX = readD();
        _originY = readD();
        _originZ = readD();
        _attackId = readC();
    }

    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.setActive();

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!activeChar.getPlayerAccess().CanAttack) {
            activeChar.sendActionFailed();
            return;
        }

        GameObject target = activeChar.getVisibleObject(_objectId);
        if (target == null) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isPendingOlyEnd()) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.getAggressionTarget() != null && activeChar.getAggressionTarget() != target && !activeChar.getAggressionTarget().isDead()) {
            activeChar.sendActionFailed();
            return;
        }

        if (target instanceof Player && (activeChar.isInBoat() || target.isInBoat())) {
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.getTarget() != target) {
            target.onAction(activeChar, false);
            return;
        }

        if (target.objectId() != activeChar.objectId() && !activeChar.isInStoreMode() && !activeChar.isProcessingRequest())
            target.onForcedAttack(activeChar, false);
    }
}