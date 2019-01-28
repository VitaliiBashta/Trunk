package l2trunk.gameserver.dao;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.skills.TimeStamp;
import l2trunk.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public final class CharacterGroupReuseDAO {
    private static final String DELETE_SQL_QUERY = "DELETE FROM character_group_reuse WHERE object_id=?";
    private static final String SELECT_SQL_QUERY = "SELECT * FROM character_group_reuse WHERE object_id=?";
    private static final String INSERT_SQL_QUERY = "REPLACE INTO `character_group_reuse` (`object_id`,`reuse_group`,`item_id`,`end_time`,`reuse`) VALUES";
    private static final Logger _log = LoggerFactory.getLogger(CharacterGroupReuseDAO.class);
    private static final CharacterGroupReuseDAO _instance = new CharacterGroupReuseDAO();

    public static CharacterGroupReuseDAO getInstance() {
        return _instance;
    }

    public static void select(Player player, Connection con) {
        long curTime = System.currentTimeMillis();

        try (PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY)) {
            statement.setInt(1, player.getObjectId());

            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    int group = rset.getInt("reuse_group");
                    int itemId = rset.getInt("item_id");
                    long endTime = rset.getLong("end_time");
                    long reuse = rset.getLong("reuse");

                    if (endTime - curTime > 500) {
                        TimeStamp stamp = new TimeStamp(itemId, endTime, reuse);
                        player.addSharedGroupReuse(group, stamp);
                    }
                }
            }
        } catch (SQLException e) {
            _log.error("CharacterGroupReuseDAO.getBonuses(L2Player) 1:", e);
        }

        try (PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY)) {
            statement.setInt(1, player.getObjectId());
            statement.execute();
        } catch (SQLException e) {
            _log.error("CharacterGroupReuseDAO.getBonuses(L2Player) 2:", e);
        }
    }

    public void insert(Player player) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY)) {
            statement.setInt(1, player.getObjectId());
            statement.execute();

            Collection<Map.Entry<Integer, TimeStamp>> reuses = player.getSharedGroupReuses();
            if (reuses.isEmpty())
                return;

            SqlBatch b = new SqlBatch(INSERT_SQL_QUERY);
            synchronized (reuses) {
                for (Map.Entry<Integer, TimeStamp> entry : reuses) {
                    int group = entry.getKey();
                    TimeStamp timeStamp = entry.getValue();
                    if (timeStamp.hasNotPassed()) {
                        String sb = "(" + player.getObjectId() + "," +
                                group + "," +
                                timeStamp.id + "," +
                                timeStamp.endTime() + "," +
                                timeStamp.getReuseBasic() + ")";
                        b.write(sb);
                    }
                }
            }
            if (!b.isEmpty())
                statement.executeUpdate(b.close());
        } catch (SQLException e) {
            _log.error("CharacterGroupReuseDAO.insert(L2Player):", e);
        }
    }
}
