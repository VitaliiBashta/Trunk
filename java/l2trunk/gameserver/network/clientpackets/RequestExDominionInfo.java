package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.network.serverpackets.ExReplyDominionInfo;
import l2trunk.gameserver.network.serverpackets.ExShowOwnthingPos;

public final class RequestExDominionInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        activeChar.sendPacket(new ExReplyDominionInfo());

        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        if (runnerEvent.isInProgress())
            activeChar.sendPacket(new ExShowOwnthingPos());
    }
}