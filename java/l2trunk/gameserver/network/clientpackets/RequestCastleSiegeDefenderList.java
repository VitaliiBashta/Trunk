package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.network.serverpackets.CastleSiegeDefenderList;

public final class RequestCastleSiegeDefenderList extends L2GameClientPacket {
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

        Castle castle = ResidenceHolder.getCastle(unitId);
        if (castle == null)
            return;

        player.sendPacket(new CastleSiegeDefenderList(castle));
    }
}