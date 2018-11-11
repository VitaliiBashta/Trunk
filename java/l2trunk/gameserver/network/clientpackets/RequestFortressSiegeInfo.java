package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.network.serverpackets.ExShowFortressSiegeInfo;

import java.util.List;

public class RequestFortressSiegeInfo extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;
        List<Fortress> fortressList = ResidenceHolder.getInstance().getResidenceList(Fortress.class);
        for (Fortress fort : fortressList)
            if (fort != null && fort.getSiegeEvent().isInProgress())
                activeChar.sendPacket(new ExShowFortressSiegeInfo(fort));
    }
}