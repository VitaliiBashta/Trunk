package l2trunk.gameserver.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class mysql {
    private static final Logger _log = LoggerFactory.getLogger(mysql.class);

    /**
     * Performs a simple sql queries where unnecessary control parameters <BR>
     * NOTE: In this method, the parameters passed are not valid for SQL-injection!
     *
     * @ Param query the SQL query string
     * @ Return false on error query or true on success
     */
    public static boolean setEx(DatabaseFactory db, String query, Object... vars) {
        Statement statement;
        PreparedStatement pstatement;
        if (db == null) db = DatabaseFactory.getInstance();
        try (Connection con = db.getConnection()) {
            if (vars.length == 0) {
                statement = con.createStatement();
                statement.executeUpdate(query);
            } else {
                pstatement = con.prepareStatement(query);
                setVars(pstatement, vars);
                pstatement.executeUpdate();
            }
        } catch (SQLException e) {
            _log.warn("Could not execute update '" + query + "' ", e);
            return false;
        }
        return true;
    }

    public static void setVars(PreparedStatement statement, Object... vars) throws SQLException {
        Number n;
        long long_val;
        double double_val;
        for (int i = 0; i < vars.length; i++)
            if (vars[i] instanceof Number) {
                n = (Number) vars[i];
                long_val = n.longValue();
                double_val = n.doubleValue();
                if (long_val == double_val)
                    statement.setLong(i + 1, long_val);
                else
                    statement.setDouble(i + 1, double_val);
            } else if (vars[i] instanceof String)
                statement.setString(i + 1, (String) vars[i]);
    }

    public static boolean set(String query, Object... vars) {
        return setEx(null, query, vars);
    }

    public static boolean set(String query) {
        return setEx(null, query);
    }



    public static Object get(String query, Object... vars) {
        Object ret = null;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(query + " LIMIT 1")){
            setVars(statement,vars);
            ResultSet rset = statement.executeQuery();
            ResultSetMetaData md = rset.getMetaData();

            if (rset.next())
                if (md.getColumnCount() > 1) {
                    Map<String, Object> tmp = new HashMap<>();
                    for (int i = md.getColumnCount(); i > 0; i--)
                        tmp.put(md.getColumnName(i), rset.getObject(i));
                    ret = tmp;
                } else
                    ret = rset.getObject(1);

        } catch (SQLException e) {
            _log.warn("Could not execute query '" + query + "' ", e);
        }
        return ret;
    }

//    public static Object get(String query) {
//        Object ret = null;
//
//        try (Connection con = DatabaseFactory.getInstance().getConnection();
//             PreparedStatement statement = con.prepareStatement(query + " LIMIT 1");
//             ResultSet rset = statement.executeQuery()) {
//            ResultSetMetaData md = rset.getMetaData();
//
//            if (rset.next())
//                if (md.getColumnCount() > 1) {
//                    Map<String, Object> tmp = new HashMap<>();
//                    for (int i = md.getColumnCount(); i > 0; i--)
//                        tmp.put(md.getColumnName(i), rset.getObject(i));
//                    ret = tmp;
//                } else
//                    ret = rset.getObject(1);
//
//        } catch (SQLException e) {
//            _log.warn("Could not execute query '" + query + "' ", e);
//        }
//        return ret;
//    }

    public static List<Map<String, Object>> getAll(String query, Object... vars) {
        List<Map<String, Object>> ret = new ArrayList<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(query)){
            setVars(statement,vars);

             ResultSet rset = statement.executeQuery(query);
            ResultSetMetaData md = rset.getMetaData();

            while (rset.next()) {
                Map<String, Object> tmp = new HashMap<>();
                for (int i = md.getColumnCount(); i > 0; i--)
                    tmp.put(md.getColumnName(i), rset.getObject(i));
                ret.add(tmp);
            }
        } catch (SQLException e) {
            _log.warn("Could not execute query '" + query + "': " + e);
        }
        return ret;
    }

    public static List<Object> get_array(DatabaseFactory db, String query) {
        List<Object> ret = new ArrayList<>();
        if (db == null) db = DatabaseFactory.getInstance();
        try (Connection con = db.getConnection();
             PreparedStatement statement = con.prepareStatement(query);
             ResultSet rset = statement.executeQuery()) {
            ResultSetMetaData md = rset.getMetaData();

            while (rset.next())
                if (md.getColumnCount() > 1) {
                    Map<String, Object> tmp = new HashMap<>();
                    for (int i = 0; i < md.getColumnCount(); i++)
                        tmp.put(md.getColumnName(i + 1), rset.getObject(i + 1));
                    ret.add(tmp);
                } else
                    ret.add(rset.getObject(1));
        } catch (SQLException e) {
            _log.warn("Could not execute query '" + query + "': ", e);
        }
        return ret;
    }

    public static List<Object> get_array(String query) {
        return get_array(null, query);
    }

    public static int simple_get_int(String ret_field, String table, String where) {
        String query = "SELECT " + ret_field + " FROM `" + table + "` WHERE " + where + " LIMIT 1;";
        int res = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(query);
             ResultSet rset = statement.executeQuery()) {

            if (rset.next())
                res = rset.getInt(1);
        } catch (SQLException e) {
            _log.warn("mSGI: Error in query '" + query + "':", e);
        }
        return res;
    }
}