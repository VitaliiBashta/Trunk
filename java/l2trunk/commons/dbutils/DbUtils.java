package l2trunk.commons.dbutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {

    public static void close(ResultSet rs) throws SQLException {
        if (rs != null)
            rs.close();
    }

    public static void close(Statement stmt) throws SQLException {
        if (stmt != null)
            stmt.close();
    }

    public static void close(Statement stmt, ResultSet rs) throws SQLException {
        close(stmt);
        close(rs);
    }

    private static void closeQuietly(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            // quiet
        }
    }

    public static void closeQuietly(Connection conn, Statement stmt) {
        closeQuietly(conn);
    }

    public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs) {
        closeQuietly(conn);
    }

}
