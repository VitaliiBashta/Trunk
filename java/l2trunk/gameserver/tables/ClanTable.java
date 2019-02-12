package l2trunk.gameserver.tables;


import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Alliance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.network.serverpackets.PledgeShowMemberListDeleteAll;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.SiegeUtils;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ClanTable {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(ClanTable.class);

    private final Map<Integer, Clan> clans = new ConcurrentHashMap<>();
    private final Map<Integer, Alliance> alliances = new ConcurrentHashMap<>();

    ClanTable() {
    }

    public void init() {
        restoreClans();
        restoreAllies();
        restoreWars();

    }

    public List<Clan> getClans() {
        List<Clan> result = new ArrayList<>(clans.values());
        Collections.sort(result);
        return result;
    }

    public List<Alliance> getAlliances() {
        return new ArrayList<>(alliances.values());
    }

    public Clan getClan(int clanId) {
        if (clanId <= 0) {
            throw new IllegalArgumentException("clan ID should be >0 ");
        }
        return clans.get(clanId);
    }

    public String getClanName(int clanId) {
        return getClan(clanId).getName();
    }

    private Clan getClanByCharId(int charId) {
        if (charId <= 0) {
            throw new IllegalArgumentException("charID can be less 0");
        }
        for (Clan clan : getClans()) {
            if (clan != null && clan.isAnyMember(charId)) {
                return clan;
            }
        }
        return null;
    }

    public Alliance getAlliance(int allyId) {
        return alliances.get(allyId);
    }

//    public Alliance getAllianceByCharId(int charId) {
////        charId.nothing();
//        if (charId <= 0) {
//            return null;
//        }
//
//        Clan charClan = getClanByCharId(charId);
//        return charClan == null ? null : charClan.getAlliance();
//    }

    public Map.Entry<Clan, Alliance> getClanAndAllianceByCharId(int charId) {
        Player player = GameObjectsStorage.getPlayer(charId);
        Clan charClan = player != null ? player.getClan() : getClanByCharId(charId);
        return new SimpleEntry<>(charClan, charClan == null ? null : charClan.getAlliance());
    }

    private void restoreClans() {
        List<Integer> clanIds = new ArrayList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT clan_id FROM clan_data");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                clanIds.add(result.getInt("clan_id"));
            }
        } catch (SQLException e) {
            LOG.warn("Error while restoring clans!!! ", e);
        }

        for (int clanId : clanIds) {
            Clan clan = Clan.restore(clanId);
            if (clan == null) {
                LOG.warn("Error while restoring clanId: " + clanId);
                continue;
            }

            if (clan.getAllSize() <= 0) {
                LOG.warn("membersCount = 0 for clanId: " + clanId);
                continue;
            }

            if (clan.getLeader() == null) {
                LOG.warn("Not found leader for clanId: " + clanId);
                continue;
            }

            clans.put(clan.clanId(), clan);
        }
    }

    private void restoreAllies() {
        List<Integer> allyIds = new ArrayList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT ally_id FROM ally_data");
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                allyIds.add(result.getInt("ally_id"));
            }
        } catch (SQLException e) {
            LOG.warn("Error while restoring allies!!! ", e);
        }

        allyIds.forEach(allyId -> {
            Alliance ally = new Alliance(allyId);
            if (ally.getMembersCount() <= 0) {
                LOG.warn("membersCount = 0 for allyId: " + allyId);
            }
            if (ally.getLeader() == null) {
                LOG.warn("Not found leader for allyId: " + allyId);
            }

            alliances.put(ally.getAllyId(), ally);
        });
    }

    public Clan getClanByName(String clanName) {
        if (!Util.isMatchingRegexp(clanName, Config.CLAN_NAME_TEMPLATE)) {
            return null;
        }

        return clans.values().stream()
                .filter(cl -> cl.getName().equalsIgnoreCase(clanName))
                .findFirst().orElse(null);
    }

    public Alliance getAllyByName(String allyName) {
        if (!Util.isMatchingRegexp(allyName, Config.ALLY_NAME_TEMPLATE)) {
            return null;
        }
        return alliances.values().stream()
                .filter(ally -> ally.getAllyName().equalsIgnoreCase(allyName))
                .findFirst().orElse(null);
    }

    public Clan createClan(Player player, String clanName) {
        if (getClanByName(clanName) == null) {
            UnitMember leader = new UnitMember(player);
            leader.setLeaderOf(Clan.SUBUNIT_MAIN_CLAN);

            Clan clan = new Clan(IdFactory.getInstance().getNextId());

            SubUnit unit = new SubUnit(clan, Clan.SUBUNIT_MAIN_CLAN, leader, clanName);
            unit.addUnitMember(leader);
            clan.addSubUnit(unit, false);

            clan.store();

            player.setPledgeType(Clan.SUBUNIT_MAIN_CLAN);
            player.setClan(clan);
            player.setPowerGrade(6);

            leader.setPlayerInstance(player, false);

            clans.put(clan.clanId(), clan);

            return clan;
        } else {
            return null;
        }
    }

    public void dissolveClan(Player player) {
        Clan clan = player.getClan();
        SiegeUtils.removeSiegeSkills(player);
        clan.getOnlineMembers(0).forEach(clanMember -> {
            clanMember.setClan(null);
            clanMember.setTitle(null);
            clanMember.sendPacket(PledgeShowMemberListDeleteAll.STATIC, SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN);
            clanMember.broadcastCharInfo();
        });
        clan.flush();
        deleteClanFromDb(clan.clanId());
        clans.remove(clan.clanId());
        player.sendPacket(SystemMsg.CLAN_HAS_DISPERSED);
    }

    private void deleteClanFromDb(int clanId) {
        long curtime = System.currentTimeMillis();

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("UPDATE characters SET clanid=0,title='',pledge_type=0,pledge_rank=0,lvl_joined_academy=0,apprentice=0,leaveclan=? WHERE clanid=?")) {
                statement.setLong(1, curtime / 1000L);
                statement.setInt(2, clanId);
                statement.execute();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_data WHERE clan_id=?")) {
                statement.setInt(1, clanId);
                statement.execute();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_subpledges WHERE clan_id=?")) {
                statement.setInt(1, clanId);
                statement.execute();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_privs WHERE clan_id=?")) {
                statement.setInt(1, clanId);
                statement.execute();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_skills WHERE clan_id=?")) {
                statement.setInt(1, clanId);
                statement.execute();
            }
        } catch (SQLException e) {
            LOG.warn("could not dissolve clan:" + e);
        }
    }

    public Alliance createAlliance(Player player, String allyName) {
        Alliance alliance = null;

        if (getAllyByName(allyName) == null) {
            Clan leader = player.getClan();
            alliance = new Alliance(IdFactory.getInstance().getNextId(), allyName, leader);
            alliance.store();
            alliances.put(alliance.getAllyId(), alliance);

            player.getClan().setAllyId(alliance.getAllyId());
            for (Player temp : player.getClan().getOnlineMembers()) {
                temp.broadcastCharInfo();
            }
        }

        return alliance;
    }

    public void dissolveAlly(Player player) {
        int allyId = player.getAllyId();
        for (Clan member : player.getAlliance().getMembers()) {
            member.setAllyId(0);
            member.broadcastClanStatus(false, true, false);
            member.broadcastToOnlineMembers(SystemMsg.YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE);
            member.setLeavedAlly();
        }
        deleteAllyFromDb(allyId);
        alliances.remove(allyId);
        player.sendPacket(SystemMsg.THE_ALLIANCE_HAS_BEEN_DISSOLVED);
        player.getClan().setDissolvedAlly();
    }

    private void deleteAllyFromDb(int allyId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {

            try (PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE ally_id=?")) {
                statement.setInt(1, allyId);
                statement.execute();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM ally_data WHERE ally_id=?")) {
                statement.setInt(1, allyId);
                statement.execute();
            }
        } catch (SQLException e) {
            LOG.warn("could not dissolve clan:" + e);
        }
    }

    public void startClanWar(Clan clan1, Clan clan2) {
        // clan1 is declaring war against clan2
        clan1.setEnemyClan(clan2);
        clan2.setAttackerClan(clan1);
        clan1.broadcastClanStatus(false, false, true);
        clan2.broadcastClanStatus(false, false, true);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO clan_wars (clan1, clan2) VALUES(?,?)")) {
            statement.setInt(1, clan1.clanId());
            statement.setInt(2, clan2.clanId());
            statement.execute();
        } catch (SQLException e) {
            LOG.warn("Could not store clan war data:", e);
        }

        clan1.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.A_CLAN_WAR_HAS_BEEN_DECLARED_AGAINST_THE_CLAN_S1).addString(clan2.getName()));
        clan2.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_DECLARED_A_CLAN_WAR).addString(clan1.getName()));
    }

    public void stopClanWar(Clan clan1, Clan clan2) {
        // clan1 is ceases war against clan2
        clan1.deleteEnemyClan(clan2);
        clan2.deleteAttackerClan(clan1);

        clan1.broadcastClanStatus(false, false, true);
        clan2.broadcastClanStatus(false, false, true);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM clan_wars WHERE clan1=? AND clan2=?")) {
            statement.setInt(1, clan1.clanId());
            statement.setInt(2, clan2.clanId());
            statement.execute();
        } catch (SQLException e) {
            LOG.warn("Could not delete war data:", e);
        }

        clan1.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_WAR_AGAINST_S1_CLAN_HAS_BEEN_STOPPED).addString(clan2.getName()));
        clan2.broadcastToOnlineMembers(new SystemMessage2(SystemMsg.THE_CLAN_S1_HAS_DECIDED_TO_STOP_THE_WAR).addString(clan1.getName()));

        // Alexander - Add a new clan war lost and won for each clan when a clan war is canceled
//		clan1.getStats().addClanStats(Ranking.STAT_TOP_CLAN_WARS_LOST);
//		clan2.getStats().addClanStats(Ranking.STAT_TOP_CLAN_WARS_WON);
    }

    private void restoreWars() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT clan1, clan2 FROM clan_wars");
             ResultSet rset = statement.executeQuery()) {
            Clan clan1;
            Clan clan2;
            while (rset.next()) {
                clan1 = getClan(rset.getInt("clan1"));
                clan2 = getClan(rset.getInt("clan2"));
                if (clan1 != null && clan2 != null) {
                    clan1.setEnemyClan(clan2);
                    clan2.setAttackerClan(clan1);
                }
            }
        } catch (SQLException e) {
            LOG.warn("Could not restore clan wars data:", e);
        }
    }
}