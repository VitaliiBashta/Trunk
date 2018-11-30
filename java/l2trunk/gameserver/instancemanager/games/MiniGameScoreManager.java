package l2trunk.gameserver.instancemanager.games;

import l2trunk.commons.dbutils.DbUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class MiniGameScoreManager {
    private static final Logger _log = LoggerFactory.getLogger(MiniGameScoreManager.class);
    private static final MiniGameScoreManager _instance = new MiniGameScoreManager();
    private final Map<Integer, Set<String>> scores = new TreeMap<>((o1, o2) -> o2 - o1);

    private MiniGameScoreManager() {
        if (Config.EX_JAPAN_MINIGAME)
            load();
    }

    public static MiniGameScoreManager getInstance() {
        return _instance;
    }

    private void load() {
        Connection con = null;
        Statement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            rset = statement.executeQuery("SELECT characters.char_name AS name, character_minigame_score.score AS score FROM characters, character_minigame_score WHERE characters.obj_Id=character_minigame_score.object_id");
            while (rset.next()) {
                String name = rset.getString("name");
                int score = rset.getInt("score");

                addScore(name, score);
            }
        } catch (SQLException e) {
            _log.info("SQLException while loading MiniGameScore: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void insertScore(Player player, int score) {
        if (addScore(player.getName(), score)) {
            Connection con = null;
            PreparedStatement statement = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("INSERT INTO character_minigame_score(object_id, score) VALUES (?, ?)");
                statement.setInt(1, player.getObjectId());
                statement.setInt(2, score);
                statement.execute();
            } catch (SQLException e) {
                _log.info("SQLException in insertScore: ", e);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }
        }
    }

    private boolean addScore(String name, int score) {
        Set<String> set = scores.get(score);
        if (set == null)
            scores.put(score, (set = new CopyOnWriteArraySet<>()));

        return set.add(name);
    }

    public Map<Integer, Set<String>> getScores() {
        return scores;
    }
}
