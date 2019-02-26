package l2trunk.gameserver.model.pledge;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.ExSubPledgeSkillAdd;
import l2trunk.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public final class SubUnit {
    private static final Logger LOG = LoggerFactory.getLogger(SubUnit.class);

    private final Map<Integer, Skill> skills = new TreeMap<>();
    private final Map<Integer, UnitMember> members = new HashMap<>();

    public final int type;
    private final Clan clan;
    private int leaderObjectId;
    private UnitMember leader;
    private String name;

    public SubUnit(Clan c, int type, UnitMember leader, String name) {
        clan = c;
        this.type = type;
        this.name = name;

        setLeader(leader, false);
    }

    public SubUnit(Clan c, int type, int leader, String name) {
        clan = c;
        this.type = type;
        leaderObjectId = leader;
        this.name = name;
    }

    private static void removeMemberInDatabase(UnitMember member) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET clanid=0, pledge_type=?, pledge_rank=0, lvl_joined_academy=0, apprentice=0, title='', leaveclan=? WHERE obj_Id=?")) {
            statement.setInt(1, Clan.SUBUNIT_NONE);
            statement.setLong(2, System.currentTimeMillis() / 1000);
            statement.setInt(3, member.objectId());
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Exception while removing Member from Sub Unit", e);
        }
    }

    public int type() {
        return type;
    }

    public String getName() {
        return name;
    }

    public UnitMember getLeader() {
        return leader;
    }

    boolean isUnitMember(int obj) {
        return members.containsKey(obj);
    }

    public void addUnitMember(UnitMember member) {
        members.put(member.objectId(), member);
    }

    public UnitMember getUnitMember(int obj) {
        if (obj == 0) {
            return null;
        }
        return members.get(obj);
    }

    public UnitMember getUnitMember(String obj) {
        return getUnitMembers().stream()
                .filter(m -> m.getName().equalsIgnoreCase(obj))
                .findFirst().orElse(null);
    }

    public void removeUnitMember(int objectId) {
        UnitMember m = members.remove(objectId);
        if (m == null) {
            return;
        }

        if (objectId == getLeaderObjectId()) // subpledge leader
            setLeader(null, true); // clan leader has to assign another one, via villagemaster

        if (m.hasSponsor())
            clan.getAnyMember(m.getSponsor()).setApprentice(0);

        removeMemberInDatabase(m);

        m.setPlayerInstance(null, true);

        // Synerge - We add a new member that withdrew from the clan to the stats
        //clan.getStats().addClanStats(Ranking.STAT_TOP_CLAN_MEMBERS_WITHDREW);
    }

    public void replace(int objectId, int newUnitId) {
        SubUnit newUnit = clan.getSubUnit(newUnitId);
        if (newUnit == null) {
            return;
        }

        UnitMember m = members.remove(objectId);
        if (m == null) {
            return;
        }

        m.setPledgeType(newUnitId);
        newUnit.addUnitMember(m);

        if (m.getPowerGrade() > 5)
            m.setPowerGrade(Clan.getAffiliationRank(m.getPledgeType()));
    }

    public int getLeaderObjectId() {
        return leader == null ? 0 : leader.objectId();
    }

    public int size() {
        return members.size();
    }

    public Collection<UnitMember> getUnitMembers() {
        return members.values();
    }

    public void setLeader(UnitMember newLeader, boolean updateDB) {
        final UnitMember old = leader;
        if (old != null)   // обновляем старого мембера
            old.setLeaderOf(Clan.SUBUNIT_NONE);

        leader = newLeader;
        leaderObjectId = newLeader == null ? 0 : newLeader.objectId();

        if (newLeader != null) {
            newLeader.setLeaderOf(type);
        }

        if (updateDB) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=? WHERE clan_id=? and type=?")) {
                statement.setInt(1, getLeaderObjectId());
                statement.setInt(2, clan.clanId());
                statement.setInt(3, type);
                statement.execute();
            } catch (SQLException e) {
                LOG.error("Exception while setting Sub Unit Leader", e);
            }
        }
    }

    public void setName(String name, boolean updateDB) {
        this.name = name;
        if (updateDB) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("UPDATE clan_subpledges SET name=? WHERE clan_id=? and type=?")) {
                statement.setString(1, this.name);
                statement.setInt(2, clan.clanId());
                statement.setInt(3, type);
                statement.execute();
            } catch (SQLException e) {
                LOG.error("Exception while setting Sub Unit Name", e);
            }
        }
    }

    public String getLeaderName() {
        return leader == null ? StringUtils.EMPTY : leader.getName();
    }

    public Skill addSkill(Skill newSkill, boolean store) {
        Skill oldSkill = null;
        if (newSkill != null) {
            // Replace oldSkill by newSkill or Add the newSkill
            oldSkill = skills.put(newSkill.id, newSkill);

            if (store) {
                try (Connection con = DatabaseFactory.getInstance().getConnection()) {
                    PreparedStatement statement;
                    if (oldSkill != null) {
                        statement = con.prepareStatement("UPDATE clan_subpledges_skills SET skill_level=? WHERE skill_id=? AND clan_id=? AND type=?");
                        statement.setInt(1, newSkill.level);
                        statement.setInt(2, oldSkill.id);
                        statement.setInt(3, clan.clanId());
                        statement.setInt(4, type);
                        statement.execute();
                    } else {
                        statement = con.prepareStatement("INSERT INTO clan_subpledges_skills (clan_id,type,skill_id,skill_level) VALUES (?,?,?,?)");
                        statement.setInt(1, clan.clanId());
                        statement.setInt(2, type);
                        statement.setInt(3, newSkill.id);
                        statement.setInt(4, newSkill.level);
                        statement.execute();
                    }
                } catch (SQLException e) {
                    LOG.warn("Exception while adding Skill to SubUnit", e);
                }
            }

            ExSubPledgeSkillAdd packet = new ExSubPledgeSkillAdd(type, newSkill.id, newSkill.level);
            for (UnitMember temp : clan)
                if (temp.isOnline()) {
                    Player player = temp.getPlayer();
                    if (player != null) {
                        player.sendPacket(packet);
                        if (player.getPledgeType() == type)
                            addSkill(player, newSkill);
                    }
                }
        }

        return oldSkill;
    }

    void addSkillsQuietly(Player player) {
        skills.values().forEach(skill -> addSkill(player, skill));
    }

    public void enableSkills(Player player) {
        for (Skill skill : skills.values())
            if (skill.minRank <= player.getPledgeClass())
                player.removeUnActiveSkill(skill);
    }

    void disableSkills(Player player) {
        for (Skill skill : skills.values())
            player.addUnActiveSkill(skill);
    }

    private void addSkill(Player player, Skill skill) {
        if (skill.minRank <= player.getPledgeClass()) {
            player.addSkill(skill, false);
            if (clan.getReputationScore() < 0 || player.isInOlympiadMode())
                player.addUnActiveSkill(skill);
        }
    }

    public Collection<Skill> getSkills() {
        return skills.values();
    }

    public void restore() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(//
                     "SELECT `c`.`char_name` AS `char_name`," + //
                             "`s`.`level` AS `level`," + //
                             "`s`.`class_id` AS `classid`," + //
                             "`c`.`obj_Id` AS `obj_id`," + //
                             "`c`.`title` AS `title`," + //
                             "`c`.`pledge_rank` AS `pledge_rank`," + //
                             "`c`.`apprentice` AS `apprentice`, " + //
                             "`c`.`sex` AS `sex` " + //
                             "FROM `characters` `c` " + //
                             "LEFT JOIN `character_subclasses` `s` ON (`s`.`char_obj_id` = `c`.`obj_Id` AND `s`.`isBase` = '1') " + //
                             "WHERE `c`.`clanid`=? AND `c`.`pledge_type`=? ORDER BY `c`.`lastaccess` DESC")) {

            statement.setInt(1, clan.clanId());
            statement.setInt(2, type);
            ResultSet rset = statement.executeQuery();

            while (rset.next()) {
                UnitMember member = new UnitMember(clan, rset.getString("char_name"), rset.getString("title"), rset.getInt("level"), rset.getInt("classid"), rset.getInt("obj_Id"), type, rset.getInt("pledge_rank"), rset.getInt("apprentice"), rset.getInt("sex"), Clan.SUBUNIT_NONE);

                addUnitMember(member);
            }

            if (type != Clan.SUBUNIT_ACADEMY) {
                SubUnit mainClan = clan.getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
                UnitMember leader = mainClan.getUnitMember(leaderObjectId);
                if (leader != null)
                    setLeader(leader, false);
                else if (type == Clan.SUBUNIT_MAIN_CLAN)
                    LOG.error("Clan " + name + " have no leader!");
            }
        } catch (SQLException e) {
            LOG.warn("Error while restoring clan members for clan: " + clan.clanId(), e);
        }
    }

    void restartMembers() {
        members.clear();
        restore();
    }

    void restoreSkills() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_subpledges_skills WHERE clan_id=? AND type=?")) {
            statement.setInt(1, clan.clanId());
            statement.setInt(2, type);
            ResultSet rset = statement.executeQuery();

            while (rset.next()) {
                int id = rset.getInt("skill_id");
                int level = rset.getInt("skill_level");

                Skill skill = SkillTable.INSTANCE.getInfo(id, level);

                skills.put(skill.id, skill);
            }
        } catch (SQLException e) {
            LOG.error("Exception while restoring Sub Unit Skills", e);
        }
    }

    public int getSkillLevel(int id, int def) {
        Skill skill = skills.get(id);
        return skill == null ? def : skill.level;
    }

    public int getSkillLevel(int id) {
        return getSkillLevel(id, -1);
    }
}
