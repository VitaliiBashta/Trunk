package l2trunk.gameserver.dao;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum CastleHiredGuardDAO {
    INSTANCE;
    private static final String SELECT_SQL_QUERY = "SELECT * FROM castle_hired_guards WHERE residence_id=?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO castle_hired_guards(residence_id, item_id, x, y, z) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM castle_hired_guards WHERE residence_id=?";
    private static final String DELETE_SQL_QUERY2 = "DELETE FROM castle_hired_guards WHERE residence_id=? AND item_id=? AND x=? AND y=? AND z=?";
    private static final Logger _log = LoggerFactory.getLogger(CastleHiredGuardDAO.class);

    public void load(Castle r) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY)) {
            statement.setInt(1, r.getId());

            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    int itemId = rset.getInt("item_id");
                    Location loc = new Location(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));

                    ItemInstance item = ItemFunctions.createItem(itemId);
                    item.spawnMe(loc);

                    r.getSpawnMerchantTickets().add(item);
                }
            }
        } catch (SQLException e) {
            _log.error("CastleHiredGuardDAO:loadFile(Castle): ", e);
        }
    }

    public void insert(Residence residence, int itemId, Location loc) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY)) {
            statement.setInt(1, residence.getId());
            statement.setInt(2, itemId);
            statement.setInt(3, loc.x);
            statement.setInt(4, loc.y);
            statement.setInt(5, loc.z);
            statement.execute();
        } catch (SQLException e) {
            _log.error("CastleHiredGuardDAO:insert(Residence, int, Location): ", e);
        }
    }

    public void delete(Residence residence, ItemInstance item) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY2)) {
            statement.setInt(1, residence.getId());
            statement.setInt(2, item.getItemId());
            statement.setInt(3, item.getLoc().x);
            statement.setInt(4, item.getLoc().y);
            statement.setInt(5, item.getLoc().z);
            statement.execute();
        } catch (SQLException e) {
            _log.error("CastleHiredGuardDAO:delete(Residence): ", e);
        }
    }

    public void delete(Residence residence) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY)) {
            statement.setInt(1, residence.getId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("CastleHiredGuardDAO:delete(Residence): ", e);
        }
    }
}
