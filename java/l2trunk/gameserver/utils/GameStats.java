package l2trunk.gameserver.utils;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.instancemanager.ServerVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public class GameStats {
    private static final Logger LOG = LoggerFactory.getLogger(GameStats.class);

    /* database statistics */
    private static final AtomicLong _updatePlayerBase = new AtomicLong(0L);

    /* in-game statistics */
    private static final AtomicLong _playerEnterGameCounter = new AtomicLong(0L);

    private static final AtomicLong _taxSum = new AtomicLong(0L);
    private static final AtomicLong _rouletteSum = new AtomicLong(0L);
    private static final AtomicLong _adenaSum = new AtomicLong(0L);
    private static long _taxLastUpdate;
    private static long _rouletteLastUpdate;

    static {
        _taxSum.set(ServerVariables.getLong("taxsum", 0));
        _rouletteSum.set(ServerVariables.getLong("rouletteSum", 0));

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT (SELECT SUM(count) FROM items WHERE item_id=57) + (SELECT SUM(treasury) FROM castle) AS `count`");
             ResultSet rset = statement.executeQuery()) {
            if (Config.RATE_DROP_ADENA < 25) {
                if (rset.next())
                    _adenaSum.addAndGet((rset.getLong("count") / (long) Config.RATE_DROP_ADENA));
            } else
                _adenaSum.addAndGet(140000000000L);
        } catch (SQLException e) {
            LOG.error("Error while getting GameStats", e);
        }
    }

    public static void increaseUpdatePlayerBase() {
        _updatePlayerBase.incrementAndGet();
    }

    public static long getUpdatePlayerBase() {
        return _updatePlayerBase.get();
    }

    public static void incrementPlayerEnterGame() {
        _playerEnterGameCounter.incrementAndGet();
    }

    public static long getPlayerEnterGame() {
        return _playerEnterGameCounter.get();
    }

    public static void addTax(long sum) {
        long taxSum = _taxSum.addAndGet(sum);
        if (System.currentTimeMillis() - _taxLastUpdate < 10000)
            return;
        _taxLastUpdate = System.currentTimeMillis();
        ServerVariables.set("taxsum", taxSum);
    }

    public static void addRoulette(long sum) {
        long rouletteSum = _rouletteSum.addAndGet(sum);
        if (System.currentTimeMillis() - _rouletteLastUpdate < 10000)
            return;
        _rouletteLastUpdate = System.currentTimeMillis();
        ServerVariables.set("rouletteSum", rouletteSum);
    }

    public static long getTaxSum() {
        return _taxSum.get();
    }

    public static long getRouletteSum() {
        return _rouletteSum.get();
    }

    public static void addAdena(long sum) {
        _adenaSum.addAndGet(sum);
    }

    public static long getAdena() {
        return _adenaSum.get();
    }
}