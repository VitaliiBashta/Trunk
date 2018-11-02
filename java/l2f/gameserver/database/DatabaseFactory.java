package l2f.gameserver.database;

import l2f.commons.dbcp.BasicDataSource;
import l2f.gameserver.Config;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseFactory extends BasicDataSource {
    private DatabaseFactory() {
        super(Config.DATABASE_DRIVER, Config.DATABASE_GAME_URL, Config.DATABASE_GAME_USER, Config.DATABASE_GAME_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
    }

    public static DatabaseFactory getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return getConnection(null);
    }

    private static class SingletonHolder {
        private static DatabaseFactory instance = new DatabaseFactory();
    }
}