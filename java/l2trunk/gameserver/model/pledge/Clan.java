package l2trunk.gameserver.model.pledge;

import l2trunk.commons.collections.JoinedIterator;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.CrestCache;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.database.mysql;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.entity.residence.ResidenceType;
import l2trunk.gameserver.model.items.ClanWarehouse;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class Clan implements Iterable<UnitMember>, Comparable<Clan> {
    public static final int CP_CL_INVITE_CLAN = 2; // Join clan
    public static final int CP_CL_MANAGE_TITLES = 4; // Give a title
    public static final int CP_CL_WAREHOUSE_SEARCH = 8; // View warehouse content
    public static final int CP_CL_MANAGE_RANKS = 16; // manage clan ranks
    public static final int CP_CL_CLAN_WAR = 32;
    public static final int CP_CL_DISMISS = 64;
    public static final int CP_CL_EDIT_CREST = 128; // Edit clan crest
    public static final int CP_CL_APPRENTICE = 256;
    public static final int CP_CL_TROOPS_FAME = 512;
    public static final int CP_CL_SUMMON_AIRSHIP = 1024;
    // Clan Privileges: clan hall
    public static final int CP_CH_ENTRY_EXIT = 2048; // open a door
    public static final int CP_CH_USE_FUNCTIONS = 4096;
    public static final int CP_CH_AUCTION = 8192;
    public static final int CP_CH_DISMISS = 16384;
    public static final int CP_CH_SET_FUNCTIONS = 32768;
    // Clan Privileges: castle/fotress
    public static final int CP_CS_ENTRY_EXIT = 65536;
    public static final int CP_CS_MANOR_ADMIN = 131072;
    public static final int CP_CS_MANAGE_SIEGE = 262144;
    public static final int CP_CS_USE_FUNCTIONS = 524288;
    public static final int CP_CS_DISMISS = 1048576;
    public static final int CP_CS_TAXES = 2097152;
    // private String _description = null;
    public static final int CP_CS_MERCENARIES = 4194304;
    public static final int CP_CS_SET_FUNCTIONS = 8388606;
    public static final int CP_ALL = 16777214;
    public static final int RANK_FIRST = 1;
    public static final int RANK_LAST = 9;
    // Sub-unit types
    public static final int SUBUNIT_NONE = Byte.MIN_VALUE;
    public static final int SUBUNIT_ACADEMY = -1;
    public static final int SUBUNIT_MAIN_CLAN = 0;
    public static final int SUBUNIT_ROYAL1 = 100;
    public static final int SUBUNIT_ROYAL2 = 200;
    public static final int SUBUNIT_KNIGHT1 = 1001;
    public static final int SUBUNIT_KNIGHT4 = 2002;
    // Clan Privileges: system
    private static final int CP_NOTHING = 0;
    private static final int SUBUNIT_KNIGHT2 = 1002;
    private static final int SUBUNIT_KNIGHT3 = 2001;
    private static final Logger _log = LoggerFactory.getLogger(Clan.class);
    // all these in milliseconds
    private static final long EXPELLED_MEMBER_PENALTY = Config.CLAN_LEAVE_PENALTY * 60 * 60 * 1000L;
    private static final long LEAVED_ALLY_PENALTY = Config.ALLY_LEAVE_PENALTY * 60 * 60 * 1000L;
    private static final long DISSOLVED_ALLY_PENALTY = Config.DISSOLVED_ALLY_PENALTY * 60 * 60 * 1000L;
    private final int clanId;
    private final Map<Integer, Skill> skills = new TreeMap<>();
    private final Map<Integer, RankPrivs> privs = new TreeMap<>();
    private final Map<Integer, SubUnit> subUnits = new TreeMap<>();
    private final List<Clan> _atWarWith = new ArrayList<>();
    private final List<Clan> _underAttackFrom = new ArrayList<>();
    private final List<Integer> classesNeeded = new ArrayList<>();
    private final List<SinglePetition> petitions = new ArrayList<>();
    private int allyId;
    private int level;
    private int hasCastle;
    private int hasFortress;
    private int _hasHideout;
    private int warDominion;
    private int _crestId;
    private int _crestLargeId;
    private long leavedAllyTime;
    private long dissolvedAllyTime;
    private long expelledMemberTime;
    private ClanAirShip airship;
    private boolean airshipLicense;
    private int airshipFuel;
    private ClanWarehouse warehouse;
    private int _whBonus = -1;
    private String notice = "";
    private int reputation = 0;
    private int _siegeKills = 0;
    // Recruitment
    private boolean recruting = false;
    private String[] questions = new String[8];

    /**
     * The constructor is only used internally to restore a database
     */
    public Clan(int clanId) {
        this.clanId = clanId;

        // Synerge - Initialize the clan stats module
        // _stats = new ClanStats(this);

        InitializePrivs();
        restoreCWH();
    }

    public static Clan restore(int clanId) {
        if (clanId == 0) // no clan
            return null;

        Clan clan = null;

        try (Connection con1 = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement1 = con1.prepareStatement("SELECT clan_level,hasCastle,hasFortress,hasHideout,ally_id,reputation_score,expelled_member,leaved_ally,dissolved_ally,warehouse,airship FROM clan_data where clan_id=?")) {
            statement1.setInt(1, clanId);
            ResultSet clanData = statement1.executeQuery();

            if (clanData.next()) {
                clan = new Clan(clanId);
                clan.setLevel(clanData.getInt("clan_level"));
                clan.setHasCastle(clanData.getInt("hasCastle"));
                clan.setHasFortress(clanData.getInt("hasFortress"));
                clan.setHasHideout(clanData.getInt("hasHideout"));
                clan.setAllyId(clanData.getInt("ally_id"));
                clan.reputation = clanData.getInt("reputation_score");
                clan.setExpelledMemberTime(clanData.getLong("expelled_member") * 1000L);
                clan.setLeavedAllyTime(clanData.getLong("leaved_ally") * 1000L);
                clan.setDissolvedAllyTime(clanData.getLong("dissolved_ally") * 1000L);
                clan.setWhBonus(clanData.getInt("warehouse"));
                clan.setAirshipLicense(clanData.getInt("airship") != -1);
                if (clan.isHaveAirshipLicense())
                    clan.setAirshipFuel(clanData.getInt("airship"));
            } else {
                _log.warn("Clan " + clanId + " doesnt exists!");
                return null;
            }
        } catch (SQLException e) {
            _log.error("Error while restoring clan!", e);
        }

        if (clan == null) {
            _log.warn("Clan " + clanId + " does't exist");
            return null;
        }

        clan.restoreSkills();
        clan.restoreSubPledges();
        clan.restoreClanRecruitment();

        clan.subUnits.values().forEach(unit -> {
            unit.restore();
            unit.restoreSkills();
        });

        clan.restoreRankPrivs();
        clan.setCrestId(CrestCache.getPledgeCrestId(clanId));
        clan.setCrestLargeId(CrestCache.getPledgeCrestLargeId(clanId));

        // Synerge - Restore all clan stats from the database
        // clan.getStats().restoreClanStats();

        return clan;
    }

    public static boolean isAcademy(int pledgeType) {
        return pledgeType == SUBUNIT_ACADEMY;
    }

    public static boolean isRoyalGuard(int pledgeType) {
        return pledgeType == SUBUNIT_ROYAL1 || pledgeType == SUBUNIT_ROYAL2;
    }

    public static boolean isOrderOfKnights(int pledgeType) {
        return pledgeType == SUBUNIT_KNIGHT1 || pledgeType == SUBUNIT_KNIGHT2 || pledgeType == SUBUNIT_KNIGHT3 || pledgeType == SUBUNIT_KNIGHT4;
    }

    public static int getAffiliationRank(int pledgeType) {
        if (isAcademy(pledgeType))
            return 9;
        else if (isOrderOfKnights(pledgeType))
            return 8;
        else if (isRoyalGuard(pledgeType))
            return 7;
        else
            return 6;
    }

    public void restoreCWH() {
        warehouse = new ClanWarehouse(this);
        warehouse.restore();
    }

    public int clanId() {
        return clanId;
    }

    public int getLeaderId() {
        return getLeaderId(SUBUNIT_MAIN_CLAN);
    }

    public UnitMember getLeader() {
        return getSubUnit(SUBUNIT_MAIN_CLAN).getLeader();
    }

    public String getLeaderName() {
        return getLeaderName(SUBUNIT_MAIN_CLAN);
    }

    public String getName() {
        return getUnitName();
    }

    public long getExpelledMemberTime() {
        return expelledMemberTime;
    }

    public void setExpelledMemberTime(long time) {
        expelledMemberTime = time;
    }

    public void setExpelledMember() {
        expelledMemberTime = System.currentTimeMillis();
        updateClanInDB();
    }

    private long getLeavedAllyTime() {
        return leavedAllyTime;
    }

    private void setLeavedAllyTime(long time) {
        leavedAllyTime = time;
    }

    public void setLeavedAlly() {
        leavedAllyTime = System.currentTimeMillis();
        updateClanInDB();
    }

    private long dissolvedAllyTime() {
        return dissolvedAllyTime;
    }

    private void setDissolvedAllyTime(long time) {
        dissolvedAllyTime = time;
    }

    public void setDissolvedAlly() {
        dissolvedAllyTime = System.currentTimeMillis();
        updateClanInDB();
    }

    public UnitMember getAnyMember(int id) {
        return subUnits.values().stream()
                .map(unit -> unit.getUnitMember(id))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    public UnitMember getAnyMember(String name) {
        return subUnits.values().stream()
                .map(unit -> unit.getUnitMember(name))
                .filter(Objects::nonNull)
                .findFirst().orElse(null);
    }

    public int getAllSize() {
        return subUnits.values().stream().mapToInt(SubUnit::size).sum();
    }

    private String getUnitName() {
        if (!subUnits.containsKey(Clan.SUBUNIT_MAIN_CLAN)) {
            return "";
        }

        return getSubUnit(Clan.SUBUNIT_MAIN_CLAN).getName();
    }

    public String getLeaderName(int unitType) {
        if (unitType == SUBUNIT_NONE || !subUnits.containsKey(unitType)) {
            return "";
        }

        return getSubUnit(unitType).getLeaderName();
    }

    public int getLeaderId(int unitType) {
        if (unitType == SUBUNIT_NONE || !subUnits.containsKey(unitType)) {
            return 0;
        }
        return getSubUnit(unitType).getLeaderObjectId();
    }


    public void flush() {
        subUnits.values().stream().flatMap(u -> u.getUnitMembers().stream())
                .forEach(m -> removeClanMember(m.objectId));
//        for (UnitMember member : collectionStream)
//            removeClanMember(member.objectId());
        warehouse.writeLock();
        try {
            for (ItemInstance item : warehouse.getItems())
                warehouse.destroyItem(item, "Flush");
        } finally {
            warehouse.writeUnlock();
        }
        if (hasCastle != 0)
            ResidenceHolder.getResidence(Castle.class, hasCastle).changeOwner(null);
        if (hasFortress != 0)
            ResidenceHolder.getResidence(Fortress.class, hasFortress).changeOwner(null);
    }

    public void removeClanMember(int id) {
        if (id == getLeaderId(SUBUNIT_MAIN_CLAN)) {
            return;
        }

        for (SubUnit unit : subUnits.values()) {
            if (unit.isUnitMember(id)) {
                removeClanMember(unit.type, id);
                break;
            }
        }
    }

    public void removeClanMember(int subUnitId, int objectId) {
        SubUnit subUnit = getSubUnit(subUnitId);
        if (subUnit == null)
            return;

        subUnit.removeUnitMember(objectId);
    }

    public List<UnitMember> getAllMembers() {
        List<UnitMember> members = new ArrayList<>();

        subUnits.values().forEach(unit -> members.addAll(unit.getUnitMembers()));
        return members;
    }

    public List<Player> getOnlineMembers(int exclude) {
        if (getAllSize() == 0)
            return new ArrayList<>();
        final List<Player> result = new ArrayList<>(getAllSize() - 1);

        for (final UnitMember temp : this)
            if (temp != null && temp.isOnline() && temp.objectId() != exclude)
                result.add(temp.getPlayer());

        return result;
    }

    public List<Player> getOnlineMembers() {
        final List<Player> result = new ArrayList<>();

        for (final UnitMember temp : this)
            if (temp != null && temp.isOnline())
                result.add(temp.getPlayer());

        return result;
    }

    public int getAllyId() {
        return allyId;
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }


    public int getCastle() {
        return hasCastle;
    }

    public int getHasFortress() {
        return hasFortress;
    }

    /**
     * Sets the fortress, which is owned by the clan. <BR>
     * At the same time possess a fortress and castle can not be
     *
     */
    public void setHasFortress(int fortress) {
        if (hasCastle == 0)
            hasFortress = fortress;
    }

    public int getHasHideout() {
        return _hasHideout;
    }

    public void setHasHideout(int hasHideout) {
        _hasHideout = hasHideout;
    }

    public int getResidenceId(ResidenceType r) {
        switch (r) {
            case Castle:
                return hasCastle;
            case Fortress:
                return hasFortress;
            case ClanHall:
                return _hasHideout;
            default:
                return 0;
        }
    }


    public void setHasCastle(int castle) {
        if (hasFortress == 0)
            hasCastle = castle;
    }

    public boolean isAnyMember(int id) {
        return  subUnits.values().stream()
        .anyMatch(u ->u.isUnitMember(id));
    }

    public void updateClanInDB() {
        if (getLeaderId() == 0) {
            _log.warn("updateClanInDB with empty LeaderId");
            Thread.dumpStack();
            return;
        }

        if (clanId() == 0) {
            _log.warn("updateClanInDB with empty ClanId");
            Thread.dumpStack();
            return;
        }
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET ally_id=?,reputation_score=?,expelled_member=?,leaved_ally=?,dissolved_ally=?,clan_level=?,warehouse=?,airship=? WHERE clan_id=?")) {
            statement.setInt(1, getAllyId());
            statement.setInt(2, getReputationScore());
            statement.setLong(3, getExpelledMemberTime() / 1000);
            statement.setLong(4, leavedAllyTime / 1000);
            statement.setLong(5, dissolvedAllyTime / 1000);
            statement.setInt(6, level);
            statement.setInt(7, getWhBonus());
            statement.setInt(8, isHaveAirshipLicense() ? getAirshipFuel() : -1);
            statement.setInt(9, clanId);
            statement.execute();

            // Synerge - Save all clan stats to the database
            // getStats().updateClanStatsToDB();
        } catch (SQLException e) {
            _log.warn("error while updating clan '" + clanId + "' data in db", e);
        }
    }

    public void store() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("INSERT INTO clan_data (clan_id,clan_level,hasCastle,hasFortress,hasHideout,ally_id,expelled_member,leaved_ally,dissolved_ally,airship) values (?,?,?,?,?,?,?,?,?,?)")) {
                statement.setInt(1, clanId);
                statement.setInt(2, level);
                statement.setInt(3, hasCastle);
                statement.setInt(4, hasFortress);
                statement.setInt(5, _hasHideout);
                statement.setInt(6, allyId);
                statement.setLong(7, expelledMemberTime / 1000);
                statement.setLong(8, leavedAllyTime / 1000);
                statement.setLong(9, dissolvedAllyTime / 1000);
                statement.setInt(10, isHaveAirshipLicense() ? getAirshipFuel() : -1);
                statement.execute();
            } catch (SQLException e) {
                _log.warn("Exception while storing Clan", e);
            }


            SubUnit mainSubUnit = subUnits.get(SUBUNIT_MAIN_CLAN);

            try (PreparedStatement statement = con.prepareStatement("INSERT INTO clan_subpledges (clan_id, type, leader_id, name) VALUES (?,?,?,?)")) {
                statement.setInt(1, clanId);
                statement.setInt(2, mainSubUnit.type());
                statement.setInt(3, mainSubUnit.getLeaderObjectId());
                statement.setString(4, mainSubUnit.getName());
                statement.execute();
            } catch (SQLException e) {
                _log.warn("Exception while storing Clan", e);
            }
            try (PreparedStatement statement = con.prepareStatement("UPDATE characters SET clanid=?,pledge_type=? WHERE obj_Id=?")) {
                statement.setInt(1, clanId);
                statement.setInt(2, mainSubUnit.type());
                statement.setInt(3, getLeaderId());
                statement.execute();
            } catch (SQLException e) {
                _log.warn("Exception while storing Clan", e);
            }
        } catch (SQLException e) {
            _log.warn("Exception while storing Clan", e);
        }
    }

    public void broadcastToOnlineMembers(IStaticPacket... packets) {
        for (UnitMember member : this)
            if (member.isOnline())
                member.getPlayer().sendPacket(packets);
    }

    public void broadcastToOnlineMembers(L2GameServerPacket... packets) {
        for (UnitMember member : this)
            if (member.isOnline())
                member.getPlayer().sendPacket(packets);
    }

    public void broadcastToOtherOnlineMembers(L2GameServerPacket packet, Player player) {
        for (UnitMember member : this)
            if (member.isOnline() && member.getPlayer() != player)
                member.getPlayer().sendPacket(packet);
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getCrestId() {
        return _crestId;
    }

    public void setCrestId(int newcrest) {
        _crestId = newcrest;
    }

    public boolean hasCrest() {
        return _crestId > 0;
    }

    public int getCrestLargeId() {
        return _crestLargeId;
    }

    public void setCrestLargeId(int newcrest) {
        _crestLargeId = newcrest;
    }

    public boolean hasCrestLarge() {
        return _crestLargeId > 0;
    }

    public long getAdenaCount() {
        return warehouse.getCountOfAdena();
    }

    public ClanWarehouse getWarehouse() {
        return warehouse;
    }

    public boolean isAtWar() {
        return !_atWarWith.isEmpty();
    }

    public int isAtWarOrUnderAttack() {
        if (!_atWarWith.isEmpty() || !_underAttackFrom.isEmpty())
            return 1;
        return 0;
    }

    public boolean isAtWarWith(int id) {
        Clan clan = ClanTable.INSTANCE.getClan(id);
        if (!_atWarWith.isEmpty())
            return _atWarWith.contains(clan);
        return false;
    }

    public boolean isUnderAttackFrom(int id) {
        Clan clan = ClanTable.INSTANCE.getClan(id);
        if (!_underAttackFrom.isEmpty())
            return _underAttackFrom.contains(clan);
        return false;
    }

    public void setEnemyClan(Clan clan) {
        _atWarWith.add(clan);
    }

    public void deleteEnemyClan(Clan clan) {
        _atWarWith.remove(clan);
    }

    // clans that are attacking this clan
    public void setAttackerClan(Clan clan) {
        _underAttackFrom.add(clan);
    }

    public void deleteAttackerClan(Clan clan) {
        _underAttackFrom.remove(clan);
    }

    public List<Clan> getEnemyClans() {
        return _atWarWith;
    }

    public int getWarsCount() {
        return _atWarWith.size();
    }

    public List<Clan> getAttackerClans() {
        return _underAttackFrom;
    }

    public void broadcastClanStatus(boolean updateList, boolean needUserInfo, boolean relation) {
        List<L2GameServerPacket> listAll = updateList ? listAll() : null;
        PledgeShowInfoUpdate update = new PledgeShowInfoUpdate(this);

        for (UnitMember member : this)
            if (member.isOnline()) {
                if (updateList) {
                    member.getPlayer().sendPacket(PledgeShowMemberListDeleteAll.STATIC);
                    member.getPlayer().sendPacket(listAll);
                }
                member.getPlayer().sendPacket(update);
                if (needUserInfo)
                    member.getPlayer().broadcastCharInfo();
                if (relation)
                    member.getPlayer().broadcastRelationChanged();
            }
    }

    public Alliance getAlliance() {
        return allyId == 0 ? null : ClanTable.INSTANCE.getAlliance(allyId);
    }

    public boolean canInvite() {
        return System.currentTimeMillis() - expelledMemberTime >= EXPELLED_MEMBER_PENALTY;
    }

    public boolean canJoinAlly() {
        return System.currentTimeMillis() - leavedAllyTime >= LEAVED_ALLY_PENALTY;
    }

    public boolean canCreateAlly() {
        return System.currentTimeMillis() - dissolvedAllyTime >= DISSOLVED_ALLY_PENALTY;
    }

    public int getRank() {
        List<Clan> clans = ClanTable.INSTANCE.getClans();
        for (int i = 0; i < clans.size(); i++) {
            if (this == clans.get(i))
                return i + 1;
        }
        return 0;
    }

    /* ============================ clan skills stuff ============================ */

    public int getReputationScore() {
        return reputation;
    }

    private void setReputationScore(int rep) {
        if (reputation >= 0 && rep < 0) {
            broadcastToOnlineMembers(Msg.SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DE_ACTIVATED);
            for (UnitMember member : this)
                if (member.isOnline() && member.getPlayer() != null)
                    disableSkills(member.getPlayer());
        } else if (reputation < 0 && rep >= 0) {
            broadcastToOnlineMembers(Msg.THE_CLAN_SKILL_WILL_BE_ACTIVATED_BECAUSE_THE_CLANS_REPUTATION_SCORE_HAS_REACHED_TO_0_OR_HIGHER);
            for (UnitMember member : this)
                if (member.isOnline() && member.getPlayer() != null)
                    enableSkills(member.getPlayer());
        }

        if (reputation != rep) {
            reputation = rep;
            broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
        }

        updateClanInDB();
    }

    public int incReputation(int inc, boolean rate, String source) {
        if (level < 5)
            return 0;

        if (rate && Math.abs(inc) <= Config.RATE_CLAN_REP_SCORE_MAX_AFFECTED)
            inc = (int) Math.round(inc * Config.RATE_CLAN_REP_SCORE);

        setReputationScore(reputation + inc);
        Log.add(getName() + "|" + inc + "|" + reputation + "|" + source, "clan_reputation");

        // Synerge - Add the new reputation to the stats
        // if (inc > 0)
        // getStats().addClanStats(Ranking.STAT_TOP_CLAN_FAME, inc);

        return inc;
    }

    public int incReputation(int inc) {
        if (level < 5)
            return 0;

        setReputationScore(reputation + inc);

        // Synerge - Add the new reputation to the stats
        // if (inc > 0)
        // getStats().addClanStats(Ranking.STAT_TOP_CLAN_FAME, inc);

        return inc;
    }

    private void restoreSkills() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_skills WHERE clan_id=?")) {
            statement.setInt(1, clanId());
            ResultSet rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next()) {
                int id = rset.getInt("skill_id");
                int level = rset.getInt("skill_level");
                // Add the L2Skill object to the L2Clan skills
                skills.put(id, SkillTable.INSTANCE.getInfo(id, level));
            }
        } catch (SQLException e) {
            _log.warn("Could not restore clan skills: ", e);
        }
    }

    public Collection<Skill> getSkills() {
        return skills.values();
    }

    public final Collection<Skill> getAllSkills() {
        if (reputation < 0)
            return List.of();

        return skills.values();
    }

    /**
     * used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db
     */
    public Skill addSkill(Skill newSkill, boolean store) {
        Skill oldSkill = null;
        if (newSkill != null) {
            // Replace oldSkill by newSkill or Add the newSkill
            oldSkill = skills.put(newSkill.id, newSkill);

            if (store) {
                PreparedStatement statement;

                try (Connection con = DatabaseFactory.getInstance().getConnection()) {

                    if (oldSkill != null) {
                        statement = con.prepareStatement("UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?");
                        statement.setInt(1, newSkill.level);
                        statement.setInt(2, oldSkill.id);
                        statement.setInt(3, clanId());
                        statement.execute();
                    } else {
                        statement = con.prepareStatement("INSERT INTO clan_skills (clan_id,skill_id,skill_level) VALUES (?,?,?)");
                        statement.setInt(1, clanId());
                        statement.setInt(2, newSkill.id);
                        statement.setInt(3, newSkill.level);
                        statement.execute();
                    }
                } catch (SQLException e) {
                    _log.warn("Error could not store char skills: ", e);
                }
            }

            PledgeSkillListAdd p = new PledgeSkillListAdd(newSkill.id, newSkill.level);
            PledgeSkillList p2 = new PledgeSkillList(this);
            for (UnitMember temp : this) {
                if (temp.isOnline()) {
                    Player player = temp.getPlayer();
                    if (player != null) {
                        addSkill(player, newSkill);
                        player.sendPacket(p, p2, new SkillList(player));
                    }
                }
            }
        }

        return oldSkill;
    }

    public void addSkillsQuietly(Player player) {
        for (Skill skill : skills.values())
            addSkill(player, skill);

        final SubUnit subUnit = getSubUnit(player.getPledgeType());
        if (subUnit != null)
            subUnit.addSkillsQuietly(player);
    }

    public void enableSkills(Player player) {
        if (player.isInOlympiadMode()) // do not allow clan skills on Olympiad
            return;

        for (Skill skill : skills.values())
            if (skill.minPledgeClass <= player.getPledgeClass())
                player.removeUnActiveSkill(skill);

        final SubUnit subUnit = getSubUnit(player.getPledgeType());
        if (subUnit != null)
            subUnit.enableSkills(player);
    }

    /* ============================ clan subpledges stuff ============================ */

    public void disableSkills(Player player) {
        for (Skill skill : skills.values())
            player.addUnActiveSkill(skill);

        final SubUnit subUnit = getSubUnit(player.getPledgeType());
        if (subUnit != null)
            subUnit.disableSkills(player);
    }

    private void addSkill(Player player, Skill skill) {
        if (skill.minPledgeClass <= player.getPledgeClass()) {
            player.addSkill(skill, false);
            if (reputation < 0 || player.isInOlympiadMode())
                player.addUnActiveSkill(skill);
        }
    }

    public void removeSkill(int skill) {
        skills.remove(skill);
        PledgeSkillListAdd p = new PledgeSkillListAdd(skill, 0);
        for (UnitMember temp : this) {
            Player player = temp.getPlayer();
            if (player != null && player.isOnline()) {
                player.removeSkill(skill);
                player.sendPacket(p, new SkillList(player));
            }
        }
    }

    public void broadcastSkillListToOnlineMembers() {
        for (UnitMember temp : this) {
            Player player = temp.getPlayer();
            if (player != null && player.isOnline()) {
                player.sendPacket(new PledgeSkillList(this));
                player.sendPacket(new SkillList(player));
            }
        }
    }

    public void restartMembers() {
        subUnits.values().forEach(SubUnit::restartMembers);
    }

    public final SubUnit getSubUnit(int pledgeType) {
        return subUnits.get(pledgeType);
    }

    public final void addSubUnit(SubUnit sp, boolean updateDb) {
        subUnits.put(sp.type(), sp);

        if (updateDb) {
            broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(sp));
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO `clan_subpledges` (clan_id,type,leader_id,name) VALUES (?,?,?,?)")) {
                statement.setInt(1, clanId());
                statement.setInt(2, sp.type());
                statement.setInt(3, sp.getLeaderObjectId());
                statement.setString(4, sp.getName());
                statement.execute();
            } catch (SQLException e) {
                _log.warn("Could not store clan Sub pledges: ", e);
            }
        }
    }

    public int createSubPledge(Player player, int pledgeType, UnitMember leader, String name) {
        int temp = pledgeType;
        pledgeType = getAvailablePledgeTypes(pledgeType);

        if (pledgeType == SUBUNIT_NONE) {
            if (temp == SUBUNIT_ACADEMY)
                player.sendPacket(Msg.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
            else
                player.sendMessage("You can't create any more sub-units of this type");
            return SUBUNIT_NONE;
        }

        switch (pledgeType) {
            case SUBUNIT_ACADEMY:
                break;
            case SUBUNIT_ROYAL1:
            case SUBUNIT_ROYAL2:
                if (getReputationScore() < 5000) {
                    player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                    return SUBUNIT_NONE;
                }
                incReputation(-5000, false, "SubunitCreate");
                break;
            case SUBUNIT_KNIGHT1:
            case SUBUNIT_KNIGHT2:
            case SUBUNIT_KNIGHT3:
            case SUBUNIT_KNIGHT4:
                if (getReputationScore() < 10000) {
                    player.sendPacket(Msg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                    return SUBUNIT_NONE;
                }
                incReputation(-10000, false, "SubunitCreate");
                break;
        }

        addSubUnit(new SubUnit(this, pledgeType, leader, name), true);
        return pledgeType;
    }

    private int getAvailablePledgeTypes(int pledgeType) {
        if (pledgeType == SUBUNIT_MAIN_CLAN)
            return SUBUNIT_NONE;

        if (subUnits.get(pledgeType) != null)
            switch (pledgeType) {
                case SUBUNIT_ACADEMY:
                    return SUBUNIT_NONE;
                case SUBUNIT_ROYAL1:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
                    break;
                case SUBUNIT_ROYAL2:
                    return SUBUNIT_NONE;
                case SUBUNIT_KNIGHT1:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
                    break;
                case SUBUNIT_KNIGHT2:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
                    break;
                case SUBUNIT_KNIGHT3:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
                    break;
                case SUBUNIT_KNIGHT4:
                    return SUBUNIT_NONE;
            }
        return pledgeType;
    }

    private void restoreSubPledges() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM clan_subpledges WHERE clan_id=?")) {
            statement.setInt(1, clanId());
            ResultSet rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next()) {
                int type = rset.getInt("type");
                int leaderId = rset.getInt("leader_id");
                String name = rset.getString("name");
                SubUnit pledge = new SubUnit(this, type, leaderId, name);
                addSubUnit(pledge, false);
            }
        } catch (SQLException e) {
            _log.warn("Could not restore clan SubPledges", e);
        }
    }

    public int getSubPledgeLimit(int pledgeType) {
        int limit;
        switch (level) {
            case 0:
                limit = 10;
                break;
            case 1:
                limit = 15;
                break;
            case 2:
                limit = 20;
                break;
            case 3:
                limit = 30;
                break;
            default:
                limit = 40;
                break;
        }
        switch (pledgeType) {
            case SUBUNIT_ACADEMY:
            case SUBUNIT_ROYAL1:
            case SUBUNIT_ROYAL2:
                if (getLevel() >= 11)
                    limit = 30;
                else
                    limit = 20;
                break;
            case SUBUNIT_KNIGHT1:
            case SUBUNIT_KNIGHT2:
                if (getLevel() >= 9)
                    limit = 25;
                else
                    limit = 10;
                break;
            case SUBUNIT_KNIGHT3:
            case SUBUNIT_KNIGHT4:
                if (getLevel() >= 10)
                    limit = 25;
                else
                    limit = 10;
                break;
        }
        return limit;
    }

    public int getUnitMembersSize(int pledgeType) {
        if (pledgeType == Clan.SUBUNIT_NONE || !subUnits.containsKey(pledgeType)) {
            return 0;
        }
        return getSubUnit(pledgeType).size();
    }

    public void addMember(Player player, int pledgeType) {
        player.sendPacket(new JoinPledge(clanId()));

        SubUnit subUnit = getSubUnit(pledgeType);
        if (subUnit == null)
            return;

        UnitMember member = new UnitMember(this, player.getName(), player.getTitle(), player.getLevel(), player.getClassId().id, player.objectId(), pledgeType, player.getPowerGrade(), player.getApprentice(), player.isMale() ? 0 : 1, Clan.SUBUNIT_NONE);
        subUnit.addUnitMember(member);

        player.setPledgeType(pledgeType);
        player.setClan(this);

        member.setPlayerInstance(player, false);

        if (pledgeType == Clan.SUBUNIT_ACADEMY)
            player.setLvlJoinedAcademy(player.getLevel());

        member.setPowerGrade(getAffiliationRank(player.getPledgeType()));

        broadcastToOtherOnlineMembers(new PledgeShowMemberListAdd(member), player);
        broadcastToOnlineMembers(new SystemMessage2(SystemMsg.S1_HAS_JOINED_THE_CLAN).addString(player.getName()), new PledgeShowInfoUpdate(this));

        // this activates the clan tab on the new member
        player.sendPacket(SystemMsg.ENTERED_THE_CLAN);
        player.sendPacket(player.getClan().listAll());
        player.updatePledgeClass();

        addSkillsQuietly(player);

        player.sendPacket(new PledgeSkillList(this));
        player.sendPacket(new SkillList(player));

        EventHolder.findEvent(player);
        if (getWarDominion() > 0) {
            DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);

            siegeEvent.updatePlayer(player, true);
        } else
            player.broadcastCharInfo();

        player.store(false);

        // Synerge - Update the clan stats with the new total member count
        // getStats().addClanStats(Ranking.STAT_TOP_CLAN_MEMBERS_COUNT, getAllSize());

        // Synerge - Add a new recruited member to the stats
        // getStats().addClanStats(Ranking.STAT_TOP_CLAN_MEMBERS_RECRUITED);
    }

    /* Recruiting members */

    private void restoreClanRecruitment() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {

            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM clan_requiements where clan_id=" + clanId());
                 ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    recruting = (rset.getInt("recruting") == 1);
                    for (String clas : rset.getString("classes").split(","))
                        if (clas.length() > 0)
                            classesNeeded.add(toInt(clas));
                    for (int i = 1; i <= 8; i++)
                        questions[(i - 1)] = rset.getString("question" + i);
                }
            }

            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM clan_petitions where clan_id=" + clanId());
                 ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    List<String> answers = new ArrayList<>();
                    for (int i = 1; i <= 8; i++)
                        answers.add(rset.getString("answer" + i));
                    petitions.add(new SinglePetition(rset.getInt("sender_id"), answers, rset.getString("comment")));
                }
            }
        } catch (NumberFormatException | SQLException e) {
            _log.error("Error while restoring Clan Recruitment", e);
        }
    }

    public void updateRecrutationData() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("INSERT INTO clan_requiements VALUES(" + clanId() + ",0,'','','','','','','','','') ON DUPLICATE KEY UPDATE recruting=?,classes=?,question1=?,question2=?,question3=?,question4=?,question5=?,question6=?,question7=?,question8=?")) {
                statement.setInt(1, (recruting ? 1 : 0));
                statement.setString(2, getClassesForData());
                for (int i = 0; i < 8; i++)
                    statement.setString(i + 3, questions[i] == null ? "" : questions[i]);
                statement.execute();
            }

            try (PreparedStatement statement = con.prepareStatement("DELETE FROM clan_petitions WHERE clan_id=" + clanId())) {
                statement.execute();
            }

            for (SinglePetition petition : getPetitions()) {
                try (PreparedStatement statement = con.prepareStatement("INSERT IGNORE INTO clan_petitions VALUES(?,?,?,?,?,?,?,?,?,?,?)")) {
                    statement.setInt(1, petition.getSenderId());
                    statement.setInt(2, clanId());
                    for (int i = 0; i < 8; i++)
                        statement.setString(i + 3, petition.getAnswers().get(i) == null ? "" : petition.getAnswers().get(i));
                    statement.setString(11, petition.getComment());
                    statement.execute();
                }
            }
        } catch (SQLException e) {
            _log.warn("Error while updating clan recruitment system on clan id '" + clanId + "' in db", e);
        }
    }

    public synchronized boolean addPetition(int senderId, List<String> answers, String comment) {
        if (getPetition(senderId) != null)
            return false;

        petitions.add(new SinglePetition(senderId, answers, comment));
        updateRecrutationData();
        Player player = World.getPlayer(getLeaderId());
        if (player != null)
            player.sendMessage("New Clan Petition has arrived!");
        return true;
    }

    public SinglePetition getPetition(int senderId) {
        return petitions.stream().filter(petition -> petition.getSenderId() == senderId).findAny().orElse(null);
    }

    public List<SinglePetition> getPetitions() {
        return petitions;
    }

    public synchronized void deletePetition(int senderId) {
        SinglePetition petition = petitions.stream().filter(p -> p.getSenderId() == senderId).findAny().orElse(null);
        if (petition != null) {
            petitions.remove(petition);
            updateRecrutationData();
        }
    }

    public void deletePetition(SinglePetition petition) {
        petitions.remove(petition);
        updateRecrutationData();
    }

    public void setRecrutating(boolean b) {
        recruting = b;
    }

    public void addClassNeeded(int clas) {
        classesNeeded.add(clas);
    }

    public void deleteClassNeeded(int clas) // iti arat cum o generez? neahm, ca cred ca stiu care-i baiu :)
    {
        int indexOfClass = classesNeeded.indexOf(clas);
        if (indexOfClass != -1)
            classesNeeded.remove(indexOfClass);
        else
            _log.warn("Tried removing inexistent class: " + clas);
    }

    private String getClassesForData() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < getClassesNeeded().size(); i++) {
            if (i != 0)
                text.append(",");
            text.append(getClassesNeeded().get(i));
        }
        return text.toString();
    }

    public List<Integer> getClassesNeeded() {
        return classesNeeded;
    }

    public boolean isRecruting() {
        return recruting;
    }

    public String[] getQuestions() {
        return questions;
    }

    public void setQuestions(String[] questions) {
        this.questions = questions;
    }

    private void restoreRankPrivs() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT privilleges,renk FROM clan_privs WHERE clan_id=?")) {
            statement.setInt(1, clanId());
            ResultSet rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next()) {
                int rank = rset.getInt("rank");
                // int party = rset.getInt("party"); - unused?
                int privileges = rset.getInt("privilleges");
                // noinspection ConstantConditions
                RankPrivs p = privs.get(rank);
                if (p != null)
                    p.setPrivs(privileges);
                else
                    _log.warn("Invalid rank value (" + rank + "), please check clan_privs table");
            }
        } catch (SQLException e) {
            _log.warn("Could not restore clan privs by rank: ", e);
        }
    }

    /* ============================ clan privilege ranks stuff ============================ */

    private void InitializePrivs() {
        for (int i = RANK_FIRST; i <= RANK_LAST; i++)
            privs.put(i, new RankPrivs(i, 0, CP_NOTHING));
    }

    public void updatePrivsForRank(int rank) {
        for (UnitMember member : this)
            if (member.isOnline() && member.getPlayer() != null && member.getPlayer().getPowerGrade() == rank) {
                if (member.getPlayer().isClanLeader())
                    continue;
                member.getPlayer().sendUserInfo();
            }
    }

    public RankPrivs getRankPrivs(int rank) {
        if (rank < RANK_FIRST || rank > RANK_LAST) {
            _log.warn("Requested invalid rank value: " + rank);
            throw new IllegalArgumentException("Requested invalid rank value: " + rank);
        }
        if (privs.get(rank) == null) {
            _log.warn("Request of rank before init: " + rank);
            Thread.dumpStack();
            setRankPrivs(rank, CP_NOTHING);
        }
        return privs.get(rank);
    }

    int countMembersByRank(int rank) {
        int ret = 0;
        for (UnitMember m : this)
            if (m.getPowerGrade() == rank)
                ret++;
        return ret;
    }

    public void setRankPrivs(int rank, int privs) {
        if (rank < RANK_FIRST || rank > RANK_LAST) {
            _log.warn("Requested set of invalid rank value: " + rank);
            Thread.dumpStack();
            return;
        }

        if (this.privs.get(rank) != null)
            this.privs.get(rank).setPrivs(privs);
        else
            this.privs.put(rank, new RankPrivs(rank, countMembersByRank(rank), privs));

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO clan_privs (clan_id,rank,privilleges) VALUES (?,?,?)")) {
            statement.setInt(1, clanId());
            statement.setInt(2, rank);
            statement.setInt(3, privs);
            statement.execute();
        } catch (SQLException e) {
            _log.warn("Could not store clan privs for rank: ", e);
        }
    }

    public final List<RankPrivs> getAllRankPrivs() {
        return new ArrayList<>(privs.values());
    }

    public int getWhBonus() {
        return _whBonus;
    }

    public void setWhBonus(int i) {
        if (_whBonus != -1)
            mysql.set("UPDATE `clan_data` SET `warehouse`=? WHERE `clan_id`=?", i, clanId());
        _whBonus = i;
    }

    public void setAirshipLicense(boolean val) {
        airshipLicense = val;
    }

    public boolean isHaveAirshipLicense() {
        return airshipLicense;
    }

    public ClanAirShip getAirship() {
        return airship;
    }

    public void setAirship(ClanAirShip airship) {
        this.airship = airship;
    }

    public int getAirshipFuel() {
        return airshipFuel;
    }

    public void setAirshipFuel(int fuel) {
        airshipFuel = fuel;
    }

    public final Collection<SubUnit> getAllSubUnits() {
        return subUnits.values();
    }

    public List<L2GameServerPacket> listAll() {
        return subUnits.values().stream()
                .map(unit -> new PledgeShowMemberListAll(this, unit))
                .collect(Collectors.toList());
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public int getSkillLevel(int id, int def) {
        Skill skill = skills.get(id);
        return skill == null ? def : skill.level;
    }

    public int getWarDominion() {
        return warDominion;
    }

    public void setWarDominion(int warDominion) {
        this.warDominion = warDominion;
    }

    public void incSiegeKills() {
        _siegeKills++;
    }

    public int getSiegeKills() {
        return _siegeKills;
    }

    public void setSiegeKills(int i) {
        _siegeKills = i;
    }

    @Override
    public Iterator<UnitMember> iterator() {
        List<Iterator<UnitMember>> iterators = new ArrayList<>(subUnits.size());
        for (SubUnit subUnit : subUnits.values())
            iterators.add(subUnit.getUnitMembers().iterator());
        return new JoinedIterator<>(iterators);
    }

    public boolean isFull() {
        return subUnits.values().stream()
                .map(SubUnit::type)
                .noneMatch(unitType -> getUnitMembersSize(unitType) < getSubPledgeLimit(unitType));
    }

    public int getAverageLevel() {
        int size = 0;
        int level = 0;

        for (SubUnit unit : subUnits.values()) {
            for (UnitMember member : unit.getUnitMembers()) {
                size++;
                level += member.getLevel();
            }
        }

        return level / size;
    }

    @Override
    public int compareTo(Clan o) {
        if (o == null) return 1;
        return this.getReputationScore() - o.getReputationScore();
    }


    public class SinglePetition {
        final int sender;
        final List<String> answers;
        final String comment;

        private SinglePetition(int sender, List<String> answers, String comment) {
            this.sender = sender;
            this.answers = answers;
            this.comment = comment;
        }

        public int getSenderId() {
            return sender;
        }

        public List<String> getAnswers() {
            return answers;
        }

        public String getComment() {
            return comment;
        }
    }

}