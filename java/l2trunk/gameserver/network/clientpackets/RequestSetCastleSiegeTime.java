package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.CastleSiegeInfo;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class RequestSetCastleSiegeTime extends L2GameClientPacket {
    private int id, time;

    @Override
    protected void readImpl() {
        id = readD();
        time = readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        Castle castle = ResidenceHolder.getCastle(id);
        if (castle == null)
            return;

        if (player.getClan().getCastle() != castle.getId())
            return;

        if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME);
            return;
        }

        CastleSiegeEvent siegeEvent = castle.getSiegeEvent();

        siegeEvent.setNextSiegeTime(time);

        player.sendPacket(new CastleSiegeInfo(castle, player));
    }
}