package l2trunk.gameserver.dao;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum ClanDataDAO {
    INSTANCE;
    private static final String SELECT_CASTLE_OWNER = "SELECT clan_id FROM clan_data WHERE hasCastle = ? LIMIT 1";
    private static final String SELECT_FORTRESS_OWNER = "SELECT clan_id FROM clan_data WHERE hasFortress = ? LIMIT 1";
    private static final String SELECT_CLANHALL_OWNER = "SELECT clan_id FROM clan_data WHERE hasHideout = ? LIMIT 1";
    private static final String UPDATE_CLAN_DESCRIPTION = "UPDATE clan_description SET description=? WHERE clan_id=?";
    private static final String INSERT_CLAN_DESCRIPTION = "INSERT INTO clan_description (clan_id, description) VALUES (?, ?)";
    private static final Logger _log = LoggerFactory.getLogger(ClanDataDAO.class);

    public Clan getOwner(Castle c) {
        return getOwner(c, SELECT_CASTLE_OWNER);
    }

    public Clan getOwner(Fortress f) {
        return getOwner(f, SELECT_FORTRESS_OWNER);
    }

    public Clan getOwner(ClanHall c) {
        return getOwner(c, SELECT_CLANHALL_OWNER);
    }

    private Clan getOwner(Residence residence, String sql) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, residence.getId());
            try (ResultSet rset = statement.executeQuery()) {
                if (rset.next())
                    return ClanTable.INSTANCE.getClan(rset.getInt("clan_id"));
            }
        } catch (SQLException e) {
            _log.error("ClanDataDAO.getOwner(Residence, String)", e);
        }
        return null;
    }

    public void updateDescription(int id, String description) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_CLAN_DESCRIPTION)) {
            statement.setString(1, description);
            statement.setInt(2, id);
            statement.execute();
        } catch (SQLException e) {
            _log.error("ClanDataDAO.updateDescription(int, String)", e);
        }
    }

    public void insertDescription(int id, String description) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_CLAN_DESCRIPTION)) {
            statement.setInt(1, id);
            statement.setString(2, description);
            statement.execute();
        } catch (SQLException e) {
            _log.error("ClanDataDAO.updateDescription(int, String)", e);
        }
    }
}
