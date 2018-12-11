package l2trunk.gameserver;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.serverpackets.Ex2ndPasswordAck;
import l2trunk.gameserver.network.serverpackets.Ex2ndPasswordCheck;
import l2trunk.gameserver.network.serverpackets.Ex2ndPasswordVerify;
import l2trunk.gameserver.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public final class SecondaryPasswordAuth {
    private static final Logger LOG = LoggerFactory.getLogger(SecondaryPasswordAuth.class);
    private static final String VAR_PWD = "secauth_pwd";
    private static final String VAR_WTE = "secauth_wte";
    private static final String SELECT_PASSWORD = "SELECT var, value FROM character_secondary_password WHERE account_name=? AND var LIKE 'secauth_%'";
    private static final String INSERT_PASSWORD = "INSERT INTO character_secondary_password VALUES (?, ?, ?)";
    private static final String UPDATE_PASSWORD = "UPDATE character_secondary_password SET value=? WHERE account_name=? AND var=?";
    private static final String INSERT_ATTEMPT = "INSERT INTO character_secondary_password VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value=?";
    private final GameClient _activeClient;
    private String _password;
    private int wrongAttempts;
    private boolean _authed;
    // private static final String BAN_ACCOUNT = "UPDATE accounts SET banExpires=? WHERE login=?";

    private SecondaryPasswordAuth(GameClient activeClient) {
        _activeClient = activeClient;
        _password = null;
        wrongAttempts = 0;
        _authed = false;
        loadPassword();
    }

    private void loadPassword() {
        String var, value;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_PASSWORD)) {
            statement.setString(1, _activeClient.getLogin());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                var = rs.getString("var");
                value = rs.getString("value");

                if (var.equals(VAR_PWD))
                    _password = value;
                else if (var.equals(VAR_WTE))
                    wrongAttempts = Integer.parseInt(value);
            }
        } catch (NumberFormatException | SQLException e) {
            LOG.error("Error while reading password.", e);
        }
    }

    public boolean savePassword(String password) {
        if (passwordExist()) {
            LOG.warn("[SecondaryPasswordAuth]" + _activeClient.getLogin() + " forced savePassword");
            _activeClient.closeNow(true);
            return false;
        }

        if (!validatePassword(password)) {
            _activeClient.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.WRONG_PATTERN));
            return false;
        }

        password = cryptPassword(password);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_PASSWORD)) {
            statement.setString(1, _activeClient.getLogin());
            statement.setString(2, VAR_PWD);
            statement.setString(3, password);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error while writing password", e);
            return false;
        }
        _password = password;
        return true;
    }

    private void insertWrongAttempt(int attempts) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_ATTEMPT)) {
            statement.setString(1, _activeClient.getLogin());
            statement.setString(2, VAR_WTE);
            statement.setString(3, Integer.toString(attempts));
            statement.setString(4, Integer.toString(attempts));
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error while writing wrong attempts", e);
        }
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (!passwordExist()) {
            LOG.warn("[SecondaryPasswordAuth]" + _activeClient.getLogin() + " forced changePassword");
            _activeClient.closeNow(true);
            return false;
        }

        if (!checkPassword(oldPassword, true))
            return false;

        if (!validatePassword(newPassword)) {
            _activeClient.sendPacket(new Ex2ndPasswordAck(Ex2ndPasswordAck.WRONG_PATTERN));
            return false;
        }

        newPassword = cryptPassword(newPassword);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_PASSWORD)) {
            statement.setString(1, newPassword);
            statement.setString(2, _activeClient.getLogin());
            statement.setString(3, VAR_PWD);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error while reading password", e);
            return false;
        }
        _password = newPassword;
        _authed = false;
        return true;
    }

    private boolean checkPassword(String password, boolean skipAuth) {
        password = cryptPassword(password);

        if (!password.equals(_password)) {
            wrongAttempts++;
            _activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_WRONG, wrongAttempts));
            insertWrongAttempt(wrongAttempts);

            return false;
        }
        if (!skipAuth) {
            _authed = true;
            _activeClient.sendPacket(new Ex2ndPasswordVerify(Ex2ndPasswordVerify.PASSWORD_OK, wrongAttempts));
        }
        insertWrongAttempt(0);
        return true;
    }

    private void ban(Player actor, long time) {
        long date = Calendar.getInstance().getTimeInMillis();
        long endban = date / 1000 + time * 60;
        String msg = "Secondary Password Auth ban Player" + actor.getName() + " on " + time + " sec";

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO bans (account_name, obj_id, baned, unban, reason, GM, endban) VALUES(?,?,?,?,?,?,?)")) {
            statement.setString(1, actor.getAccountName());
            statement.setInt(2, actor.getObjectId());
            statement.setString(3, "SU");
            statement.setString(4, "SU");
            statement.setString(5, msg);
            statement.setString(6, "SU");
            statement.setLong(7, endban);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Could not store bans data:", e);
        }
    }

    private boolean passwordExist() {
        return _password != null;
    }

    public void openDialog() {
        if (passwordExist())
            _activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_PROMPT));
        else
            _activeClient.sendPacket(new Ex2ndPasswordCheck(Ex2ndPasswordCheck.PASSWORD_NEW));
    }

    public boolean isAuthed() {
        return _authed;
    }

    private String cryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] raw = password.getBytes(StandardCharsets.UTF_8);
            byte[] hash = md.digest(raw);
            return java.util.Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("[SecondaryPasswordAuth]Unsupported Algorythm", e);
        }
        return null;
    }

    private boolean validatePassword(String password) {
        if (!Util.isDigit(password))
            return false;

        if (password.length() < 6 || password.length() > 8)
            return false;
        wrongAttempts = 0;
        return true;
    }
}