package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class ClanHallNpcSiegeEvent extends SiegeEvent<ClanHall, SiegeClanObject> {
    public ClanHallNpcSiegeEvent(StatsSet set) {
        super(set);
    }

    @Override
    public void startEvent() {
        oldOwner = getResidence().getOwner();

        broadcastInZone(new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName(getResidence()));

        super.startEvent();
    }

    @Override
    public void stopEvent(boolean step) {
        Clan newOwner = getResidence().getOwner();
        if (newOwner != null) {
            if (oldOwner != newOwner) {
                newOwner.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);

                newOwner.incReputation(1700, false, toString());

                if (oldOwner != null)
                    oldOwner.incReputation(-1700, false, toString());
            }

            broadcastInZone(new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(newOwner.getName()).addResidenceName(getResidence()));
            broadcastInZone(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName(getResidence()));
        } else
            broadcastInZone(new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName(getResidence()));

        super.stopEvent(step);

        oldOwner = null;
    }

    @Override
    public void processStep(Clan clan) {
        if (clan != null)
            getResidence().changeOwner(clan);

        stopEvent(true);
    }

    @Override
    public void loadSiegeClans() {
        //
    }
}
