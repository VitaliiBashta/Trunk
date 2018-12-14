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
    private final int _dominionId;
    private final String _ownerClanName;
    private final String _ownerLeaderName;
    private final String _ownerAllyName;
    private final int _clanReq;
    private final int _mercReq;
    private final int _warTime;
    private final int _currentTime;
    private final boolean _registeredAsPlayer;
    private final boolean _registeredAsClan;
    private final List<TerritoryFlagsInfo> flags;

    public ExShowDominionRegistry(Player activeChar, Dominion dominion) {
        _dominionId = dominion.getId();

        Clan owner = dominion.getOwner();
        Alliance alliance = owner.getAlliance();

        DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
        _ownerClanName = owner.getName();
        _ownerLeaderName = owner.getLeaderName();
        _ownerAllyName = alliance == null ? StringUtils.EMPTY : alliance.getAllyName();
        _warTime = (int) (dominion.getSiegeDate().getTimeInMillis() / 1000L);
        _currentTime = (int) (System.currentTimeMillis() / 1000L);
        _mercReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).size();
        _clanReq = siegeEvent.getObjects(DominionSiegeEvent.DEFENDERS).size() + 1;
        _registeredAsPlayer = siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(activeChar.getObjectId());
        _registeredAsClan = siegeEvent.getSiegeClan(DominionSiegeEvent.DEFENDERS, activeChar.getClan()) != null;

        List<Dominion> dominions = ResidenceHolder.getResidenceList(Dominion.class);
        flags = dominions.stream()
                .map(d -> new TerritoryFlagsInfo(d.getId(), d.getFlags()))
                .collect(Collectors.toList());
    }

    @Override
    protected void writeImpl() {
        writeEx(0x90);

        writeD(_dominionId);
        writeS(_ownerClanName);
        writeS(_ownerLeaderName);
        writeS(_ownerAllyName);
        writeD(_clanReq); // Clan Request
        writeD(_mercReq); // Merc Request
        writeD(_warTime); // War Time
        writeD(_currentTime); // Current Time
        writeD(_registeredAsClan); // Состояние клановой кнопки: 0 - не подписал, 1 - подписан на эту территорию
        writeD(_registeredAsPlayer); // Состояние персональной кнопки: 0 - не подписал, 1 - подписан на эту территорию
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