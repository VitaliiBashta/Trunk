package l2trunk.gameserver.dao;

import l2trunk.commons.dbutils.DbUtils;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;


public class DominionRewardDAO {
    private static final String INSERT_SQL_QUERY = "INSERT INTO dominion_rewards (id, object_id, static_badges, online_reward, kill_reward) VALUES";
    private static final String SELECT_SQL_QUERY = "SELECT * FROM dominion_rewards WHERE id=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM dominion_rewards WHERE id=? AND object_id=?";
    private static final String DELETE_SQL_QUERY2 = "DELETE FROM dominion_rewards WHERE id=?";

    private static final Logger _log = LoggerFactory.getLogger(DominionRewardDAO.class);
    private static final DominionRewardDAO _instance = new DominionRewardDAO();

    public static DominionRewardDAO getInstance() {
        return _instance;
    }

    public void select(Dominion d) {
        DominionSiegeEvent siegeEvent = d.getSiegeEvent();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY)) {
            statement.setInt(1, d.getId());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    int playerObjectId = rset.getInt("object_id");
                    int staticBadges = rset.getInt("static_badges");
                    int onlineReward = rset.getInt("online_reward");
                    int killReward = rset.getInt("kill_reward");

                    siegeEvent.setReward(playerObjectId, DominionSiegeEvent.STATIC_BADGES, staticBadges);
                    siegeEvent.setReward(playerObjectId, DominionSiegeEvent.KILL_REWARD, killReward);
                    siegeEvent.setReward(playerObjectId, DominionSiegeEvent.ONLINE_REWARD, onlineReward);
                }
            }
        } catch (SQLException e) {
            _log.error("DominionRewardDAO:getBonuses(Dominion): " + e, e);
        }
    }

    public void insert(Dominion d) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DELETE_SQL_QUERY2);
            statement.setInt(1, d.getId());
            statement.execute();

            DominionSiegeEvent siegeEvent = d.getSiegeEvent();
            Collection<Map.Entry<Integer, int[]>> rewards = siegeEvent.getRewards();

            SqlBatch b = new SqlBatch(INSERT_SQL_QUERY);
            for (Map.Entry<Integer, int[]> entry : rewards) {
                String sb = "(" + d.getId() + "," +
                        entry.getKey() + "," +
                        entry.getValue()[DominionSiegeEvent.STATIC_BADGES] + "," +
                        entry.getValue()[DominionSiegeEvent.ONLINE_REWARD] + "," +
                        entry.getValue()[DominionSiegeEvent.KILL_REWARD] + ")";
                b.write(sb);
            }

            if (!b.isEmpty())
                statement.executeUpdate(b.close());
        } catch (SQLException e) {
            _log.error("DominionRewardDAO.insert(Dominion):", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void delete(Dominion d, int objectId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY)) {
            statement.setInt(1, d.getId());
            statement.setInt(2, objectId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("DominionRewardDAO:delete(Dominion): ", e);
        }
    }
}
