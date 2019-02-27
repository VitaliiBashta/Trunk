package l2trunk.gameserver.instancemanager;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ServerVariables {
    private static final Logger LOG = LoggerFactory.getLogger(ServerVariables.class);

    private static StatsSet server_vars = new StatsSet();

    static {
        LoadFromDB();
    }

    private ServerVariables() {
    }

    private static void LoadFromDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM server_variables");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next())
                server_vars.set(rs.getString("name"), rs.getString("value"));
        } catch (SQLException e) {
            LOG.error("Error while Loading Server Variables ", e);
        }
    }

    private static void SaveToDB(String name) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement;
            String value = server_vars.getString(name, "");
            if (value.isEmpty()) {
                statement = con.prepareStatement("DELETE FROM server_variables WHERE name = ?");
                statement.setString(1, name);
                statement.execute();
            } else {
                statement = con.prepareStatement("REPLACE INTO server_variables (name, value) VALUES (?,?)");
                statement.setString(1, name);
                statement.setString(2, value);
                statement.execute();
            }
        } catch (SQLException e) {
            LOG.error("Error while Saving Server Variables! ", e);
        }
    }

    public static boolean getBool(String name) {
        return server_vars.isSet(name);
    }

    public static boolean getBool(String name, boolean defult) {
        return server_vars.getBool(name, defult);
    }

    public static int getInt(String name) {
        return server_vars.getInteger(name);
    }

    public static int getInt(String name, int defult) {
        return server_vars.getInteger(name, defult);
    }

    public static long getLong(String name) {
        return server_vars.getLong(name);
    }

    public static long getLong(String name, long defult) {
        return server_vars.getLong(name, defult);
    }

    public static String getString(String name) {
        return server_vars.getString(name);
    }

    public static String getString(String name, String _default) {
        return server_vars.getString(name, _default);
    }

    public static void set(String name, boolean value) {
        server_vars.set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, int value) {
        server_vars.set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, long value) {
        server_vars.set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, double value) {
        server_vars.set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, String value) {
        server_vars.set(name, value);
        SaveToDB(name);
    }

    public static void unset(String name) {
        server_vars.unset(name);
        SaveToDB(name);
    }
}