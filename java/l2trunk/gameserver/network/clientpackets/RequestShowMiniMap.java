package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.network.serverpackets.ShowMiniMap;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class RequestShowMiniMap extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        // Map of Hellbound
        if (activeChar.isActionBlocked(Zone.BLOCKED_ACTION_MINIMAP) ||
                (activeChar.isInZone("[Hellbound_territory]") && !activeChar.haveItem(9994))) {
            activeChar.sendPacket(SystemMsg.THIS_IS_AN_AREA_WHERE_YOU_CANNOT_USE_THE_MINI_MAP);
            return;
        }

        sendPacket(new ShowMiniMap(0));
    }
}