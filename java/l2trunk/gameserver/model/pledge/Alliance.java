package l2trunk.gameserver.model.pledge;

import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Alliance {
    private static final Logger _log = LoggerFactory.getLogger(Alliance.class);
    private static final long EXPELLED_MEMBER_PENALTY = 24 * 60 * 60 * 1000L;
    private final Map<Integer, Clan> members = new ConcurrentHashMap<>();
    private String allyName;
    private int allyId;
    private Clan leader = null;
    private int allyCrestId;
    private long expelledMemberTime;

    public Alliance(int allyId) {
        this.allyId = allyId;
        restore();
    }

    public Alliance(int allyId, String allyName, Clan leader) {
        this.allyId = allyId;
        this.allyName = allyName;
        setLeader(leader);
    }

    private int getLeaderId() {
        return leader != null ? leader.clanId() : 0;
    }

    public Clan getLeader() {
        return leader;
    }

    private void setLeader(Clan leader) {
        this.leader = leader;
        members.put(leader.clanId(), leader);
    }

    public String getAllyLeaderName() {
        return leader != null ? leader.getLeaderName() : "";
    }

    public void addAllyMember(Clan member, boolean storeInDb) {
        members.put(member.clanId(), member);

        if (storeInDb)
            storeNewMemberInDatabase(member);
    }

    public void removeAllyMember(int id) {
        if (leader != null && leader.clanId() == id)
            return;
        Clan exMember = members.remove(id);
        if (exMember == null) {
            _log.warn("Clan " + id + " not found in alliance while trying to remove");
            return;
        }
        removeMemberInDatabase(exMember);
    }

    public List<Clan> getMembers() {
        return new ArrayList<>(members.values());
    }

    public int getMembersCount() {
        return members.size();
    }

    public int getAllyId() {
        return allyId;
    }

    public String getAllyName() {
        return allyName;
    }

    public int getAllyCrestId() {
        return allyCrestId;
    }

    public void setAllyCrestId(int allyCrestId) {
        this.allyCrestId = allyCrestId;
    }

    public boolean isMember(int id) {
        return members.containsKey(id);
    }

    private long getExpelledMemberTime() {
        return expelledMemberTime;
    }

    public void setExpelledMember() {
        expelledMemberTime = System.currentTimeMillis();
        updateAllyInDB();
    }

    public boolean canInvite() {
        return System.currentTimeMillis() - expelledMemberTime >= EXPELLED_MEMBER_PENALTY;
    }

    private void updateAllyInDB() {
        if (getLeaderId() == 0) {
            _log.warn("updateAllyInDB with empty LeaderId");
            Thread.dumpStack();
            return;
        }

        if (allyId == 0) {
            _log.warn("updateAllyInDB with empty AllyId");
            Thread.dumpStack();
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE ally_data SET leader_id=?,expelled_member=? WHERE ally_id=?")) {
            statement.setInt(1, getLeaderId());
            statement.setLong(2, getExpelledMemberTime() / 1000);
            statement.setInt(3, allyId);
            statement.execute();
        } catch (SQLException e) {
            _log.warn("error while updating ally '" + allyId + "' data in db: ", e);
        }
    }

    public void store() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("INSERT INTO ally_data (ally_id,ally_name,leader_id) values (?,?,?)");
            statement.setInt(1, allyId);
            statement.setString(2, allyName);
            statement.setInt(3, getLeaderId());
            statement.execute();

            statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
            statement.setInt(1, allyId);
            statement.setInt(2, getLeaderId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("error while saving new ally to db ", e);
        }
    }

    private void storeNewMemberInDatabase(Clan member) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?")) {
            statement.setInt(1,allyId);
            statement.setInt(2, member.clanId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("error while saving new alliance member to db ", e);
        }
    }

    private void removeMemberInDatabase(Clan member) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE clan_id=?")) {
            statement.setInt(1, member.clanId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("Error while removing ally member in db ", e);
        }
    }

    private void restore() {
        if (allyId == 0) // no ally
            return;
        PreparedStatement statement;
        ResultSet rset;
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            statement = con.prepareStatement("SELECT ally_name,leader_id FROM ally_data where ally_id=?");
            statement.setInt(1, allyId);
            rset = statement.executeQuery();
            Clan member;

            if (rset.next()) {
                allyName =rset.getString("ally_name");
                int leaderId = rset.getInt("leader_id");

                statement = con.prepareStatement("SELECT clan_id FROM clan_data WHERE ally_id=?");
                statement.setInt(1, allyId);
                rset = statement.executeQuery();

                while (rset.next()) {
                    member = ClanTable.INSTANCE.getClan(rset.getInt("clan_id"));
                    if (member != null)
                        if (member.clanId() == leaderId)
                            setLeader(member);
                        else
                            addAllyMember(member, false);
                }
            }

            setAllyCrestId(CrestCache.getAllyCrestId(allyId));
        } catch (SQLException e) {
            _log.warn("error while restoring ally", e);
        }
    }

    public void broadcastToOnlineMembers(L2GameServerPacket packet) {
        members.values().forEach(m -> m.broadcastToOnlineMembers(packet));
    }

    @Override
    public String toString() {
        return allyName;
    }

    public boolean hasAllyCrest() {
        return allyCrestId > 0;
    }

    public void broadcastAllyStatus() {
        getMembers().forEach(m -> m.broadcastClanStatus(false, true, false));
    }
}