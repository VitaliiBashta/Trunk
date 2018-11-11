package l2trunk.gameserver.database;

import l2trunk.commons.dbcp.BasicDataSource;
import l2trunk.gameserver.Config;

public final class DatabaseFactory extends BasicDataSource {
    private DatabaseFactory() {
        super(Config.DATABASE_DRIVER, Config.DATABASE_GAME_URL, Config.DATABASE_GAME_USER, Config.DATABASE_GAME_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
    }

    public static DatabaseFactory getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final DatabaseFactory instance = new DatabaseFactory();
    }
}