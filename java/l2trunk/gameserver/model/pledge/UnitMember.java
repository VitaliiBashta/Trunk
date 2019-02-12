package l2trunk.gameserver.model.pledge;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NickNameChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class UnitMember {
    private static final Logger _log = LoggerFactory.getLogger(UnitMember.class);
    final int objectId;
    public Player player;
    private Clan clan;
    private String name;
    private String title;
    private int level;
    private int classId;
    private int sex;
    private int pledgeType;
    private int powerGrade;
    private int apprentice;

    private int leaderOf = Clan.SUBUNIT_NONE;

    public UnitMember(Clan clan, String name, String title, int level, int classId, int objectId, int pledgeType, int powerGrade, int apprentice, int sex, int leaderOf) {
        this.clan = clan;
        this.objectId = objectId;
        this.name = name;
        this.title = title;
        this.level = level;
        this.classId = classId;
        this.pledgeType = pledgeType;
        this.powerGrade = powerGrade;
        this.apprentice = apprentice;
        this.sex = sex;
        this.leaderOf = leaderOf;

        if (powerGrade != 0) {
            RankPrivs r = clan.getRankPrivs(powerGrade);
            r.setParty(clan.countMembersByRank(powerGrade));
        }
    }

    public UnitMember(Player player) {
        objectId = player.objectId;
        this.player = player;
    }

    public void setPlayerInstance(Player player, boolean exit) {
        this.player = exit ? null : player;
        if (player == null)
            return;

        clan = player.getClan();
        name = player.getName();
        title = player.getTitle();
        level = player.getLevel();
        classId = player.getClassId().id;
        pledgeType = player.getPledgeType();
        powerGrade = player.getPowerGrade();
        apprentice = player.getApprentice();
        sex = player.isMale()? 0:1;
    }

    public final Player player() {
        return player;
    }

    public boolean isOnline() {
        return player != null;
    }

    public Clan getClan() {
        return player == null ? clan : player.getClan();
    }

    public int getClassId() {
        return player == null ? classId : player.getClassId().id;
    }

    public int getSex() {
        return player == null ? sex : player.isMale()? 0:1;
    }

    public int getLevel() {
        return player == null ? level : player.getLevel();
    }

    public String getName() {
        return player == null ? name : player.getName();
    }

    public int objectId() {
        return objectId;
    }

    public String getTitle() {
        return player == null ? title : player.getTitle();
    }

    public void setTitle(String title) {
        this.title = title;
        if (player != null) {
            player.setTitle(title);
            player.broadcastPacket(new NickNameChanged(player));
        } else {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("UPDATE characters SET title=? WHERE obj_Id=?")) {
                statement.setString(1, title);
                statement.setInt(2, objectId);
                statement.execute();
            } catch (SQLException e) {
                _log.error("Error while setting Unit Member Title", e);
            }
        }
    }

    public SubUnit getSubUnit() {
        return clan.getSubUnit(pledgeType);
    }

    public int getPledgeType() {
        return player == null ? pledgeType : player.getPledgeType();
    }

    void setPledgeType(int pledgeType) {
        this.pledgeType = pledgeType;
        if (player != null)
            player.setPledgeType(pledgeType);
        else
            updatePledgeType();
    }

    private void updatePledgeType() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET pledge_type=? WHERE obj_Id=?")) {
            statement.setInt(1, pledgeType);
            statement.setInt(2, objectId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while updating Unit Member Pledge Type", e);
        }
    }

    public int getPowerGrade() {
        return player == null ? powerGrade : player.getPowerGrade();
    }

    public void setPowerGrade(int newPowerGrade) {
        int oldPowerGrade = getPowerGrade();
        powerGrade = newPowerGrade;
        if (player != null)
            player.setPowerGrade(newPowerGrade);
        else
            updatePowerGrade();
        updatePowerGradeParty(oldPowerGrade, newPowerGrade);
    }

    private void updatePowerGradeParty(int oldGrade, int newGrade) {
        if (oldGrade != 0) {
            RankPrivs r1 = getClan().getRankPrivs(oldGrade);
            r1.setParty(getClan().countMembersByRank(oldGrade));
        }
        if (newGrade != 0) {
            RankPrivs r2 = getClan().getRankPrivs(newGrade);
            r2.setParty(getClan().countMembersByRank(newGrade));
        }
    }

    private void updatePowerGrade() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET pledge_rank=? WHERE obj_Id=?")) {
            statement.setInt(1, powerGrade);
            statement.setInt(2, objectId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while updating Unit Member Power Grade", e);
        }
    }

    private int getApprentice() {
        return player == null ? apprentice : player.getApprentice();
    }

    public void setApprentice(int apprentice) {
        this.apprentice = apprentice;
        if (player != null)
            player.setApprentice(apprentice);
        else
            updateApprentice();
    }

    private void updateApprentice() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET apprentice=? WHERE obj_Id=?")) {
            statement.setInt(1, apprentice);
            statement.setInt(2, objectId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while updating Unit Member Apprentice", e);
        }
    }

    private String getApprenticeName() {
        if (getApprentice() != 0)
            if (getClan().getAnyMember(getApprentice()) != null)
                return getClan().getAnyMember(getApprentice()).getName();
        return "";
    }

    public boolean hasApprentice() {
        return getApprentice() != 0;
    }

    public int getSponsor() {
        if (getPledgeType() != Clan.SUBUNIT_ACADEMY)
            return 0;
        int id = objectId();
        for (UnitMember element : getClan())
            if (element.getApprentice() == id)
                return element.objectId();
        return 0;
    }

    private String getSponsorName() {
        int sponsorId = getSponsor();
        if (sponsorId == 0)
            return "";
        else if (getClan().getAnyMember(sponsorId) != null)
            return getClan().getAnyMember(sponsorId).getName();
        return "";
    }

    public boolean hasSponsor() {
        return getSponsor() != 0;
    }

    public String getRelatedName() {
        if (getPledgeType() == Clan.SUBUNIT_ACADEMY)
            return getSponsorName();
        return getApprenticeName();
    }

    public boolean isClanLeader() {
        Player player = player();
        return player == null ? (leaderOf == Clan.SUBUNIT_MAIN_CLAN) : player.isClanLeader();
    }

    public boolean isSubLeader() {
        return getClan().getAllSubUnits().stream()
                .filter(pledge -> pledge.getLeaderObjectId() == objectId())
                .map(SubUnit::type)
                .findFirst().isPresent();
    }

    public int getLeaderOf() {
        return leaderOf;
    }

    public void setLeaderOf(int leaderOf) {
        this.leaderOf = leaderOf;
    }
}