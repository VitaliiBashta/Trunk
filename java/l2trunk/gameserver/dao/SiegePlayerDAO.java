package l2trunk.gameserver.dao;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.entity.residence.Residence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public enum SiegePlayerDAO {
    INSTANCE;
    private static final String INSERT_SQL_QUERY = "INSERT INTO siege_players(residence_id, object_id, clan_id) VALUES (?,?,?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM siege_players WHERE residence_id=? AND object_id=? AND clan_id=?";
    private static final String DELETE_SQL_QUERY2 = "DELETE FROM siege_players WHERE residence_id=?";
    private static final String SELECT_SQL_QUERY = "SELECT object_id FROM siege_players WHERE residence_id=? AND clan_id=?";
    private static final Logger _log = LoggerFactory.getLogger(SiegePlayerDAO.class);

    public List<Integer> select(Residence residence, int clanId) {
        List<Integer> set = new ArrayList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY)) {
            statement.setInt(1, residence.getId());
            statement.setInt(2, clanId);
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next())
                    set.add(rset.getInt("object_id"));
            }
        } catch (SQLException e) {
            _log.error("SiegePlayerDAO.getBonuses(Residence, int): " + e, e);
        }
        return set;
    }

    public void insert(Residence residence, int clanId, int playerId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY)) {
            statement.setInt(1, residence.getId());
            statement.setInt(2, playerId);
            statement.setInt(3, clanId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("SiegePlayerDAO.insert(Residence, int, int): " + e, e);
        }
    }

    public void delete(Residence residence, int clanId, int playerId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY)) {
            statement.setInt(1, residence.getId());
            statement.setInt(2, playerId);
            statement.setInt(3, clanId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("SiegePlayerDAO.delete(Residence, int, int): " + e, e);
        }
    }

    public void delete(Residence residence) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY2);) {
            statement.setInt(1, residence.getId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("SiegePlayerDAO.delete(Residence): " + e, e);
        }
    }
}
