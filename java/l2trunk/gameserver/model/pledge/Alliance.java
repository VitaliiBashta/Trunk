package l2trunk.gameserver.model.pledge;

import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
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

public class Alliance {
    private static final Logger _log = LoggerFactory.getLogger(Alliance.class);
    private static final long EXPELLED_MEMBER_PENALTY = 24 * 60 * 60 * 1000L;
    private final Map<Integer, Clan> _members = new ConcurrentHashMap<>();
    private String _allyName;
    private int _allyId;
    private Clan _leader = null;
    private int _allyCrestId;
    private long _expelledMemberTime;

    public Alliance(int allyId) {
        _allyId = allyId;
        restore();
    }

    public Alliance(int allyId, String allyName, Clan leader) {
        _allyId = allyId;
        _allyName = allyName;
        setLeader(leader);
    }

    private int getLeaderId() {
        return _leader != null ? _leader.getClanId() : 0;
    }

    public Clan getLeader() {
        return _leader;
    }

    private void setLeader(Clan leader) {
        _leader = leader;
        _members.put(leader.getClanId(), leader);
    }

    public String getAllyLeaderName() {
        return _leader != null ? _leader.getLeaderName() : "";
    }

    public void addAllyMember(Clan member, boolean storeInDb) {
        _members.put(member.getClanId(), member);

        if (storeInDb)
            storeNewMemberInDatabase(member);
    }

    public Clan getAllyMember(int id) {
        return _members.get(id);
    }

    public void removeAllyMember(int id) {
        if (_leader != null && _leader.getClanId() == id)
            return;
        Clan exMember = _members.remove(id);
        if (exMember == null) {
            _log.warn("Clan " + id + " not found in alliance while trying to remove");
            return;
        }
        removeMemberInDatabase(exMember);
    }

    public List<Clan> getMembers() {
        return new ArrayList<>(_members.values());
    }

    public int getMembersCount() {
        return _members.size();
    }

    public int getAllyId() {
        return _allyId;
    }

    public void setAllyId(int allyId) {
        _allyId = allyId;
    }

    public String getAllyName() {
        return _allyName;
    }

    private void setAllyName(String allyName) {
        _allyName = allyName;
    }

    public int getAllyCrestId() {
        return _allyCrestId;
    }

    public void setAllyCrestId(int allyCrestId) {
        _allyCrestId = allyCrestId;
    }

    public boolean isMember(int id) {
        return _members.containsKey(id);
    }

    private long getExpelledMemberTime() {
        return _expelledMemberTime;
    }

    public void setExpelledMemberTime(long time) {
        _expelledMemberTime = time;
    }

    public void setExpelledMember() {
        _expelledMemberTime = System.currentTimeMillis();
        updateAllyInDB();
    }

    public boolean canInvite() {
        return System.currentTimeMillis() - _expelledMemberTime >= EXPELLED_MEMBER_PENALTY;
    }

    private void updateAllyInDB() {
        if (getLeaderId() == 0) {
            _log.warn("updateAllyInDB with empty LeaderId");
            Thread.dumpStack();
            return;
        }

        if (getAllyId() == 0) {
            _log.warn("updateAllyInDB with empty AllyId");
            Thread.dumpStack();
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE ally_data SET leader_id=?,expelled_member=? WHERE ally_id=?")) {
            statement.setInt(1, getLeaderId());
            statement.setLong(2, getExpelledMemberTime() / 1000);
            statement.setInt(3, getAllyId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("error while updating ally '" + _allyId + "' data in db: ", e);
        }
    }

    public void store() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("INSERT INTO ally_data (ally_id,ally_name,leader_id) values (?,?,?)");
            statement.setInt(1, getAllyId());
            statement.setString(2, getAllyName());
            statement.setInt(3, getLeaderId());
            statement.execute();

            statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
            statement.setInt(1, getAllyId());
            statement.setInt(2, getLeaderId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("error while saving new ally to db ", e);
        }
    }

    private void storeNewMemberInDatabase(Clan member) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?")) {
            statement.setInt(1, getAllyId());
            statement.setInt(2, member.getClanId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("error while saving new alliance member to db ", e);
        }
    }

    private void removeMemberInDatabase(Clan member) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE clan_id=?")) {
            statement.setInt(1, member.getClanId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("Error while removing ally member in db ", e);
        }
    }

    private void restore() {
        if (getAllyId() == 0) // no ally
            return;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            Clan member;

            statement = con.prepareStatement("SELECT ally_name,leader_id FROM ally_data where ally_id=?");
            statement.setInt(1, getAllyId());
            rset = statement.executeQuery();

            if (rset.next()) {
                setAllyName(rset.getString("ally_name"));
                int leaderId = rset.getInt("leader_id");

                statement = con.prepareStatement("SELECT clan_id FROM clan_data WHERE ally_id=?");
                statement.setInt(1, getAllyId());
                rset = statement.executeQuery();

                while (rset.next()) {
                    member = ClanTable.INSTANCE.getClan(rset.getInt("clan_id"));
                    if (member != null)
                        if (member.getClanId() == leaderId)
                            setLeader(member);
                        else
                            addAllyMember(member, false);
                }
            }

            setAllyCrestId(CrestCache.getAllyCrestId(getAllyId()));
        } catch (SQLException e) {
            _log.warn("error while restoring ally", e);
        }
    }

    public void broadcastToOnlineMembers(L2GameServerPacket packet) {
        for (Clan member : _members.values())
            if (member != null)
                member.broadcastToOnlineMembers(packet);
    }

    public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, Player player) {
        for (Clan member : _members.values())
            if (member != null)
                member.broadcastToOtherOnlineMembers(packet, player);
    }

    @Override
    public String toString() {
        return getAllyName();
    }

    public boolean hasAllyCrest() {
        return _allyCrestId > 0;
    }

    public void broadcastAllyStatus() {
        for (Clan member : getMembers())
            member.broadcastClanStatus(false, true, false);
    }
}