package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.model.pledge.Alliance;
import l2trunk.gameserver.model.pledge.Clan;

import java.util.List;
import java.util.stream.Collectors;

public final class ExShowDominionRegistry extends L2GameServerPacket {
    private final int dominionId;
    private final String ownerClanName;
    private final String ownerLeaderName;
    private final String ownerAllyName;
    private final int clanReq;
    private final int mercReq;
    private final int warTime;
    private final int currentTime;
    private final boolean registeredAsPlayer;
    private final boolean registeredAsClan;
    private final List<TerritoryFlagsInfo> flags;

    public ExShowDominionRegistry(Player activeChar, Dominion dominion) {
        dominionId = dominion.getId();

        Clan owner = dominion.getOwner();
        Alliance alliance = owner.getAlliance();

        DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
        ownerClanName = owner.getName();
        ownerLeaderName = owner.getLeaderName();
        ownerAllyName = alliance == null ? StringUtils.EMPTY : alliance.getAllyName();
        warTime = (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L);
        currentTime = (int) (System.currentTimeMillis() / 1000L);
        mercReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).size();
        clanReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDERS).size() + 1;
        registeredAsPlayer = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(activeChar.objectId());
        registeredAsClan = siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, activeChar.getClan()) != null;

        flags = ResidenceHolder.getDominions().stream()
                .map(d -> new TerritoryFlagsInfo(d.getId(), d.getFlags()))
                .collect(Collectors.toList());
    }

    @Override
    protected void writeImpl() {
        writeEx(0x90);

        writeD(dominionId);
        writeS(ownerClanName);
        writeS(ownerLeaderName);
        writeS(ownerAllyName);
        writeD(clanReq); // Clan Request
        writeD(mercReq); // Merc Request
        writeD(warTime); // War Time
        writeD(currentTime); // Current Time
        writeD(registeredAsClan); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
        writeD(registeredAsPlayer); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
        writeD(0x01);
        writeD(flags.size()); // Territory Count
        flags.forEach(cf -> {
            writeD(cf.id); // Territory Id
            writeD(cf.flags.size()); // Emblem Count
            cf.flags.forEach(this::writeD); // Emblem ID - should be in for loop for emblem count
        });
    }

    private class TerritoryFlagsInfo {
        final int id;
        final List<Integer> flags;

        TerritoryFlagsInfo(int id, List<Integer> flags) {
            this.id = id;
            this.flags = flags;
        }
    }
}