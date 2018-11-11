package l2trunk.gameserver.dao;

import javafx.util.Pair;
import l2trunk.commons.dbutils.DbUtils;
import l2trunk.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class AccountBonusDAO {
    private static final String SELECT_SQL_QUERY = "SELECT bonus, bonus_expire FROM account_bonus WHERE account=?";
    private static final String DELETE_SQL_QUERY = "DELETE FROM account_bonus WHERE account=?";
    private static final String INSERT_SQL_QUERY = "REPLACE INTO account_bonus(account, bonus, bonus_expire) VALUES (?,?,?)";
    private static final Logger _log = LoggerFactory.getLogger(AccountBonusDAO.class);
    private static final AccountBonusDAO _instance = new AccountBonusDAO();

    public static AccountBonusDAO getInstance() {
        return _instance;
    }

    public Pair<Integer, Integer> getBonuses(String account) {
        int bonus = 0;
        int time = 0;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_SQL_QUERY)) {
            statement.setString(1, account);
            ResultSet rset = statement.executeQuery();
            if (rset.next()) {
                bonus = rset.getInt("bonus");
                time = rset.getInt("bonus_expire");
            }
        } catch (Exception e) {
            _log.error("AccountBonusDAO.getBonuses(String): " + e, e);
        }
        return new Pair<>(bonus, time);
    }

    public void delete(String account) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_SQL_QUERY)) {
            statement.setString(1, account);
            statement.execute();
        } catch (Exception e) {
            _log.error("AccountBonusDAO.delete(String): " + e, e);
        }
    }

    public void insert(String account, double bonus, int endTime) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, account);
            statement.setDouble(2, bonus);
            statement.setInt(3, endTime);
            statement.execute();
        } catch (Exception e) {
            _log.error("AccountBonusDAO.insert(String, double, int): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }
}
