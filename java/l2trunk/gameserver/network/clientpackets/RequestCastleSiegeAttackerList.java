package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.network.serverpackets.CastleSiegeAttackerList;

public final class RequestCastleSiegeAttackerList extends L2GameClientPacket {
    private int unitId;

    @Override
    protected void readImpl() {
        unitId = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Residence residence = ResidenceHolder.getResidence(unitId);
        if (residence != null)
            sendPacket(new CastleSiegeAttackerList(residence));
    }
}