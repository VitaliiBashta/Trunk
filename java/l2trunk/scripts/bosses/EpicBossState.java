package l2trunk.scripts.bosses;

import l2trunk.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class EpicBossState {
    private static final Logger LOG = LoggerFactory.getLogger(EpicBossState.class);
    // Synerge - Support for storing the bosses status here in a static array
    private static final Map<Integer, EpicBossState> EPICS = new HashMap<>();
    private int bossId;
    private long respawnDate;
    private State state;

    public EpicBossState(int bossId) {
        this.bossId = bossId;
        load();
        EPICS.put(bossId, this);
    }

    public static Collection<EpicBossState> getEpics() {
        return EPICS.values();
    }

    public static EpicBossState getState(int epicId) {
        return EPICS.get(epicId);
    }

    public int getBossId() {
        return bossId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public long getRespawnDate() {
        return respawnDate;
    }

    void setRespawnDate(long interval) {
        respawnDate = interval + System.currentTimeMillis();
    }

    public void setRespawnDateFull(long time) {
        respawnDate = time;
    }

    private void load() {

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM epic_boss_spawn WHERE bossId = ? LIMIT 1")) {
            statement.setInt(1, bossId);
            ResultSet rset = statement.executeQuery();

            if (rset.next()) {
                respawnDate = rset.getLong("respawnDate") * 1000L;

                if (respawnDate - System.currentTimeMillis() <= 0)
                    state = State.NOTSPAWN;
                else {
                    int tempState = rset.getInt("state");
                    if (tempState == State.NOTSPAWN.ordinal())
                        state = State.NOTSPAWN;
                    else if (tempState == State.INTERVAL.ordinal())
                        state = State.INTERVAL;
                    else if (tempState == State.ALIVE.ordinal())
                        state = State.ALIVE;
                    else if (tempState == State.DEAD.ordinal())
                        state = State.DEAD;
                    else
                        state = State.NOTSPAWN;
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while loading Epic Boss States", e);
        }
    }

    public void save() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO epic_boss_spawn (bossId,respawnDate,state) VALUES(?,?,?)")) {
            statement.setInt(1, bossId);
            statement.setInt(2, (int) (respawnDate / 1000));
            statement.setInt(3, state.ordinal());
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error while saving Epic Boss States", e);
        }
    }

    public void update() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement statement = con.createStatement()) {
            statement.executeUpdate("UPDATE epic_boss_spawn SET respawnDate=" + respawnDate / 1000 + ", state=" + state.ordinal() + " WHERE bossId=" + bossId);
            final Date dt = new Date(respawnDate);
            LOG.info("update EpicBossState: ID:" + bossId + ", RespawnDate:" + dt + ", State:" + state.toString());
        } catch (SQLException e) {
            LOG.error("Exception on update EpicBossState: ID " + bossId + ", RespawnDate:" + respawnDate / 1000 + ", State:" + state.toString(), e);
        }
    }

    public long getInterval() {
        long interval = respawnDate - System.currentTimeMillis();
        return interval > 0 ? interval : 0;
    }

    public enum State {
        NOTSPAWN,
        ALIVE,
        DEAD,
        INTERVAL
    }
}
