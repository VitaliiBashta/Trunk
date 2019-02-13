package l2trunk.gameserver.instancemanager.games;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArraySet;

public enum MiniGameScoreManager {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(MiniGameScoreManager.class);
    private final Map<Integer, Set<String>> scores = new TreeMap<>((o1, o2) -> o2 - o1);

    public void init() {
        if (Config.EX_JAPAN_MINIGAME)
            load();
    }

    private void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT characters.char_name AS name, character_minigame_score.score AS score FROM characters, character_minigame_score WHERE characters.obj_Id=character_minigame_score.object_id");
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                addScore(rset.getString("name"), rset.getInt("score"));
            }
        } catch (SQLException e) {
            LOG.info("SQLException while loading MiniGameScore: " + e, e);
        }
    }

    public void insertScore(Player player, int score) {
        if (addScore(player.getName(), score)) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement("INSERT INTO character_minigame_score(object_id, score) VALUES (?, ?)")) {
                statement.setInt(1, player.objectId());
                statement.setInt(2, score);
                statement.execute();
            } catch (SQLException e) {
                LOG.info("SQLException in insertScore: ", e);
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
