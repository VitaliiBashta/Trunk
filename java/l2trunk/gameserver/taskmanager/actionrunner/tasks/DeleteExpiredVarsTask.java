package l2trunk.gameserver.taskmanager.actionrunner.tasks;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.database.mysql;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class DeleteExpiredVarsTask extends AutomaticTask {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteExpiredVarsTask.class);

    public DeleteExpiredVarsTask() {
        super();
    }

    @Override
    public void doTask() {
        Map<Integer, String> varMap = new HashMap<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement query = con.prepareStatement("SELECT obj_id, name FROM character_variables WHERE expire_time > 0 AND expire_time < ?")) {
            query.setLong(1, System.currentTimeMillis());
            try (ResultSet rs = query.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String obj_id = Strings.stripSlashes(rs.getString("obj_id"));
                    varMap.put(toInt(obj_id), name);
                }
            }
        } catch (NumberFormatException | SQLException e) {
            LOG.error("Error while Selecting Expired Character Variables", e);
        }

        if (!varMap.isEmpty()) {
            for (Map.Entry<Integer, String> entry : varMap.entrySet()) {
                Player player = GameObjectsStorage.getPlayer(entry.getKey());
                if (player != null && player.isOnline())
                    player.unsetVar(entry.getValue());
                else
                    mysql.set("DELETE FROM `character_variables` WHERE `obj_id`=? AND `type`='user-var' AND `name`=? LIMIT 1", entry.getKey(), entry.getValue());
            }
        }
    }

}
