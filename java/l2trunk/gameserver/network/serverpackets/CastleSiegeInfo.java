package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Alliance;
import l2trunk.gameserver.model.pledge.Clan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * packet type id 0xc9<BR>
 * format: cdddSSdSdd<BR>
 * <BR>
 * c = c9<BR>
 * d = UnitID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = Size of Siege Time Select Related
 * d - next siege time
 *
 * @reworked VISTALL
 */
public final class CastleSiegeInfo extends L2GameServerPacket {
    private final int id;
    private final int ownerObjectId;
    private long startTime;
    private int allyId;
    private boolean isLeader;
    private String ownerName = "No owner";
    private String leaderName = StringUtils.EMPTY;
    private String allyName = StringUtils.EMPTY;
    private List<Integer> nextTimeMillis = new ArrayList<>();

    public CastleSiegeInfo(Castle castle, Player player) {
        this((Residence) castle, player);

        CastleSiegeEvent siegeEvent = castle.getSiegeEvent();
        long siegeTimeMillis = castle.getSiegeDate().getTimeInMillis();
        if (siegeTimeMillis == 0)
            nextTimeMillis = siegeEvent.getNextSiegeTimes();
        else
            startTime = (int) (siegeTimeMillis / 1000);
    }

    public CastleSiegeInfo(ClanHall ch, Player player) {
        this((Residence) ch, player);

        startTime = (int) (ch.getSiegeDate().getTimeInMillis() / 1000);
    }

    private CastleSiegeInfo(Residence residence, Player player) {
        id = residence.getId();
        ownerObjectId = residence.getOwnerId();
        Clan owner = residence.getOwner();
        if (owner != null) {
            isLeader = player.isGM() || owner.getLeaderId(Clan.SUBUNIT_MAIN_CLAN) == player.objectId();
            ownerName = owner.getName();
            leaderName = owner.getLeaderName(Clan.SUBUNIT_MAIN_CLAN);
            Alliance ally = owner.getAlliance();
            if (ally != null) {
                allyId = ally.getAllyId();
                allyName = ally.getAllyName();
            }
        }
    }

    @Override
    protected void writeImpl() {
        writeC(0xC9);
        writeD(id);
        writeD(isLeader ? 0x01 : 0x00);
        writeD(ownerObjectId);
        writeS(ownerName); // Clan Name
        writeS(leaderName); // Clan Leader Name
        writeD(allyId); // Ally ID
        writeS(allyName); // Ally Name
        writeD((int) (Calendar.getInstance().getTimeInMillis() / 1000));
        writeD((int) startTime);
        if (startTime == 0) // If zero is the cycle
            writeDD(nextTimeMillis);
    }
}