package l2trunk.loginserver.database;

import l2trunk.commons.dbcp.BasicDataSource;
import l2trunk.loginserver.Config;


public class L2DatabaseFactory extends BasicDataSource {
    private static final L2DatabaseFactory _instance = new L2DatabaseFactory();

    private L2DatabaseFactory() {
        super(Config.DATABASE_DRIVER, Config.DATABASE_URL, Config.DATABASE_LOGIN, Config.DATABASE_PASSWORD, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_CONNECTIONS, Config.DATABASE_MAX_IDLE_TIMEOUT, Config.DATABASE_IDLE_TEST_PERIOD, false);
    }

    public static L2DatabaseFactory getInstance() {
        return _instance;
    }

}