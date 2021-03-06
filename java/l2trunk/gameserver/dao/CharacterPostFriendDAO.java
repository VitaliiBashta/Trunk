package l2trunk.gameserver.dao;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CharacterPostFriendDAO {
    private static final Logger _log = LoggerFactory.getLogger(CharacterPostFriendDAO.class);
    private static final CharacterPostFriendDAO _instance = new CharacterPostFriendDAO();

    private static final String SELECT_SQL_QUERY = "SELECT pf.post_friend, c.char_name FROM character_post_friends pf LEFT JOIN characters c ON pf.post_friend = c.obj_Id WHERE pf.object_id = ?";
    private static final String INSERT_SQL_QUERY = "INSERT INTO character_post_friends(object_id, post_friend) VALUES (?,?)";
    private static final String DELETE_SQL_QUERY = "DELETE FROM character_post_friends WHERE object_id=? AND post_friend=?";

    public static CharacterPostFriendDAO getInstance() {
        return _instance;
    }

    public static Map<Integer, String> select(Player player, Connection con) {
        Map<Integer, String> set = new HashMap<>();
        try (PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY)) {
            statement.setInt(1, player.objectId());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    if ((rset.getInt(1) <= 0) || (rset.getString(2) == null))
                        continue;
                    set.put(rset.getInt(1), rset.getString(2));
                }
            }
        } catch (SQLException e) {
            _log.error("CharacterPostFriendDAO.loadFile(Player): " + e, e);
        }
        return set;
    }

    public static void delete(Player player, int val) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY)) {
            statement.setInt(1, player.objectId());
            statement.setInt(2, val);
            statement.execute();
        } catch (SQLException e) {
            _log.error("CharacterPostFriendDAO.delete(Player, int): " + e, e);
        }
    }

    public void insert(Player player, int val) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_SQL_QUERY)) {
            statement.setInt(1, player.objectId());
            statement.setInt(2, val);
            statement.execute();
        } catch (SQLException e) {
            _log.error("CharacterPostFriendDAO.insert(Player, int): ", e);
        }
    }
}
