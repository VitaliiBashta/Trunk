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
    private static final Logger _log = LoggerFactory.getLogger(ServerVariables.class);

    private static StatsSet server_vars = null;

    private static StatsSet getVars() {
        if (server_vars == null) {
            server_vars = new StatsSet();
            LoadFromDB();
        }
        return server_vars;
    }

    private static void LoadFromDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT * FROM server_variables");
             ResultSet rs = statement.executeQuery()) {
            while (rs.next())
                server_vars.set(rs.getString("name"), rs.getString("value"));
        } catch (SQLException e) {
            _log.error("Error while Loading Server Variables ", e);
        }
    }

    private static void SaveToDB(String name) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement;
            String value = getVars().getString(name, "");
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
            _log.error("Error while Saving Server Variables! ", e);
        }
    }

    public static boolean getBool(String name) {
        return getVars().getBool(name);
    }

    public static boolean getBool(String name, boolean defult) {
        return getVars().getBool(name, defult);
    }

    public static int getInt(String name) {
        return getVars().getInteger(name);
    }

    public static int getInt(String name, int defult) {
        return getVars().getInteger(name, defult);
    }

    public static long getLong(String name) {
        return getVars().getLong(name);
    }

    public static long getLong(String name, long defult) {
        return getVars().getLong(name, defult);
    }

    public static String getString(String name) {
        return getVars().getString(name);
    }

    public static String getString(String name, String _default) {
        return getVars().getString(name, _default);
    }

    public static void set(String name, boolean value) {
        getVars().set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, int value) {
        getVars().set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, long value) {
        getVars().set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, double value) {
        getVars().set(name, value);
        SaveToDB(name);
    }

    public static void set(String name, String value) {
        getVars().set(name, value);
        SaveToDB(name);
    }

    public static void unset(String name) {
        getVars().unset(name);
        SaveToDB(name);
    }
}