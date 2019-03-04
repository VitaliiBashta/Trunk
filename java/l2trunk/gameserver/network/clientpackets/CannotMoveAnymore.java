package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Location;

public final class CannotMoveAnymore extends L2GameClientPacket {
    private Location loc;

    @Override
    protected void readImpl() {
        loc = Location.of(readD(), readD(), readD(), readD());
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.getAI().notifyEvent(CtrlEvent.EVT_ARRIVED_BLOCKED, loc);
    }
}