package l2trunk.gameserver.database;

import l2trunk.commons.dbcp.BasicDataSource;
import l2trunk.gameserver.Config;

public final class LoginDatabaseFactory extends BasicDataSource {
    private static final LoginDatabaseFactory _instance = new LoginDatabaseFactory();

    private LoginDatabaseFactory() {
        super(Config.DATABASE_DRIVER,
                Config.DATABASE_LOGIN_URL,
                Config.DATABASE_LOGIN_USER,
                Config.DATABASE_LOGIN_PASSWORD,
                Config.DATABASE_MAX_CONNECTIONS,
                Config.DATABASE_MAX_CONNECTIONS,
                Config.DATABASE_MAX_IDLE_TIMEOUT,
                Config.DATABASE_IDLE_TEST_PERIOD, false);
    }

    public static LoginDatabaseFactory getInstance() {
        return _instance;
    }

}