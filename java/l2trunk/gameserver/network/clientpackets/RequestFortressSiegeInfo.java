package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.network.serverpackets.ExShowFortressSiegeInfo;

import java.util.List;

public final class RequestFortressSiegeInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        ResidenceHolder.getResidenceList(Fortress.class).stream()
                .filter(fort -> fort.getSiegeEvent().isInProgress())
                .forEach(fort -> activeChar.sendPacket(new ExShowFortressSiegeInfo(fort)));
    }
}