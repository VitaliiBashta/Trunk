package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Location;

public class CannotMoveAnymore extends L2GameClientPacket {
    private final Location _loc = new Location();

    @Override
    protected void readImpl() {
        _loc.x = readD();
        _loc.y = readD();
        _loc.z = readD();
        _loc.h = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, _loc);
    }
}