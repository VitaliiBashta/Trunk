//package l2trunk.gameserver.dao;
//
//import l2trunk.gameserver.database.LoginDatabaseFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public final class AccountReportDAO {
//    public static final String SELECT_SQL_QUERY = "SELECT bot_report_points FROM accounts WHERE login = ?";
//    public static final String UPDATE_SQL_QUERY = "UPDATE accounts SET bot_report_points = ? WHERE login = ?";
//    private static final Logger _log = LoggerFactory.getLogger(AccountReportDAO.class);
//    private static final AccountReportDAO _instance = new AccountReportDAO(null);
//    private int _reportBotPoints;
//
//    private AccountReportDAO(String accName) {
//        loadBotPoints(accName);
//    }
//
//    public static AccountReportDAO getInstance() {
//        return _instance;
//    }
//
//    private void loadBotPoints(String accName) {
//        try (Connection con = LoginDatabaseFactory.getInstance().getConnection();
//             PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY)) {
//            statement.setString(1, accName);
//
//            try (ResultSet rset = statement.executeQuery()) {
//                while (rset.next()) {
//                    _reportBotPoints = rset.getInt("bot_report_points");
//                }
//            }
//        } catch (SQLException e) {
//            _log.info("AccountReportDAO.loadBotPoints(String): " + e, e);
//        }
//    }
//
//    public synchronized void updatePoints(String accName) {
//        try (Connection con = LoginDatabaseFactory.getInstance().getConnection();
//             PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY)) {
//            statement.setInt(1, _reportBotPoints);
//            statement.setString(2, accName);
//            statement.execute();
//        } catch (SQLException e) {
//            _log.info("AccountReportDAO.updatePoints(String): ", e);
//        }
//    }
//
//    public synchronized int getReportsPoints() {
//        return _reportBotPoints;
//    }
//
//    public synchronized void reducePoints() {
//        _reportBotPoints--;
//    }
//}