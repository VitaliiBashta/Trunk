package l2trunk.gameserver.utils;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.TutorialCloseHtml;
import l2trunk.gameserver.network.serverpackets.TutorialShowHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class AccountEmail {
    private static final Logger _log = LoggerFactory.getLogger(AccountEmail.class.getName());


    public static void onBypass(Player player, String fullbypass) {
        if (fullbypass.startsWith("setemail")) {
            // setemail email1 email2
            String[] split = fullbypass.split(" ");
            if (split.length < 3) {
                player.sendMessage("Please fill all the fields before proceeding.");
                return;
            }

            //split[1] = command
            String email1 = split[1];
            String email2 = split[2];

            setEmail(player, email1, email2);
        } else if (fullbypass.startsWith("verifyemail")) {
            String[] split = fullbypass.split(" ");
            verifyEmail(player, split.length <= 1 ? null : split[1]);
        }
    }

    public static void checkEmail(Player player) {
        if (getEmail(player) == null) // Player has no e-mail set.
        {
            String html = HtmCache.INSTANCE.getNotNull("custom/AccountEmail.htm", player);
            player.sendPacket(new TutorialShowHtml(html));
        }
    }

    public static void verifyEmail(Player player, String email) {
        if (email == null) {
            String html = HtmCache.INSTANCE.getNotNull("custom/VerifyEmail.htm", player);
            player.sendPacket(new TutorialShowHtml(html));
            if (!player.isBlocked())
                player.setBlock(true);
        } else {
            if (email.equalsIgnoreCase(getEmail(player))) {
                if (player.isBlocked())
                    player.setBlock();
                player.sendMessage("You have confirmed to be the owner of this account. You are free to go.");
                player.setVar("LastIP", player.getIP()); // Taken from EnterWorld and commented it there.
                player.sendPacket(TutorialCloseHtml.STATIC); // Close the tutorial window since you have confirmed to be the owner.
            } else {
                player.sendMessage("This is an incorrect e-mail address. You will be kicked.");
                player.getNetConnection().closeLater();
            }
        }
    }

    private static void setEmail(Player player, String email) {
        if (player != null)
            setEmail(player.getAccountName(), email);
    }

    private static void setEmail(String accountName, String email) {
        if (accountName == null)
            return;

        insertAccountData(accountName, "email_addr", email);
    }

    private static String getEmail(Player player) {
        return player != null ? getEmail(player.getAccountName()) : null;
    }

    private static String getEmail(String accountName) {
        if (accountName == null)
            return null;

        return getAccountValue(accountName, "email_addr");
    }

    private static boolean validateEmail(String email, String email2) {

        if (email == null || email2 == null || email.isEmpty() || email2.isEmpty())
            return false;

        if (email.contains("@") && email.contains(".") && email.length() <= 50 && email.length() >= 5) {
            return email.equalsIgnoreCase(email2);
        }

        return false;
    }

    private static void setEmail(Player player, String email, String confirmEmail) {
        if (!validateEmail(email, confirmEmail)) {
            player.sendMessage("This e-mail address is invalid. Please try again with a valid one.");
            player.sendMessage("It is important to use a valid e-mail address because it is the only thing to recognize you as the owner of this account.");
            // Tutorial window still not closed so the getPlayer can set e-mail again.
        } else {
            setEmail(player, email);
            player.sendMessage("Your e-mail has been successfully set to: " + email);
            player.sendMessage("Please remember this e-mail address, because it is used to identify you as the owner of this account.");
            player.sendPacket(TutorialCloseHtml.STATIC); // Close the tutorial window since now the e-mail is set.
        }
    }

    private static void insertAccountData(String accountName, String var, String value) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO account_variables VALUES (?,?,?)")) {
            statement.setString(1, accountName);
            statement.setString(2, var);
            statement.setString(3, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            _log.warn("Cannot insert account variable.", e);
        }
    }

    private static String getAccountValue(String accountName, String var) {
        String data = null;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT value FROM account_variables WHERE account_name=? AND var=?")) {
            statement.setString(1, accountName);
            statement.setString(2, var);
            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                data = rset.getString(1);
            }
        } catch (SQLException e) {
            _log.warn("Cannot get account variable value.", e);
        }

        return data;
    }

    public static String getAccountVar(String accountName) {
        String data = "";
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT var FROM account_variables WHERE account_name=?")) {
            statement.setString(1, accountName);
            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                data = rset.getString(1);
            }
        } catch (SQLException e) {
            _log.warn("Cannot get account variable.", e);
        }

        return data;
    }
}
