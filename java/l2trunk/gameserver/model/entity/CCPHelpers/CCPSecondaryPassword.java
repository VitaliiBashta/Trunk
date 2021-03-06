package l2trunk.gameserver.model.entity.CCPHelpers;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.StringTokenizer;

public final class CCPSecondaryPassword {
    private static final Logger _log = LoggerFactory.getLogger(CCPSecondaryPassword.class);

    public static void startSecondaryPasswordSetup(Player player, String text) {
        StringTokenizer st = new StringTokenizer(text, "|");
        String[] args = new String[st.countTokens()];
        for (int i = 0; i < args.length; i++)
            args[i] = st.nextToken().trim();

        String pageIndex = args[0].substring(args[0].length() - 1);

        if (pageIndex.equals("F")) {
            if (hasPassword(player))
                sendHtml(player, HtmCache.INSTANCE.getNotNull("command/cfgSPSecondaryChange.htm", player));
            else
                sendHtml(player, HtmCache.INSTANCE.getNotNull("command/cfgSPSecondarySet.htm", player));
            return;
        }
        if (args.length < 2) {
            player.sendMessage("Incorrect values!");
            return;
        }

        switch (pageIndex) {
            case "C":
                String currentPass = args[1];
                String newPass = args.length > 2 ? args[2] : "";

                if (currentPass.equals(getSecondaryPass(player).orElse(""))) {
                    setSecondaryPassword(player, player.getAccountName(), newPass);
                } else {
                    player.kick();
                }
                break;
            case "S":
                setSecondaryPassword(player, player.getAccountName(), args[1]);
                break;
        }

    }

    private static void setSecondaryPassword(Player changer, String accountName, String password) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE accounts SET secondaryPassword=? WHERE login=?")) {
            statement.setString(1, password);
            statement.setString(2, accountName);

            statement.execute();
        } catch (SQLException e) {
            _log.info("Error setSecondaryPassword ", e);
        }
        changer.sendMessage("Password Changed!");
    }

    public static boolean tryPass(Player player, String pass) {
        String correctPass = getSecondaryPass(player).orElse("");
        return pass.equalsIgnoreCase(correctPass);
    }

    public static boolean hasPassword(Player player) {
        return getSecondaryPass(player).orElse("").length() > 0;
    }

    private static void sendHtml(Player player, String html) {
        html = html.replace("%online%", CCPSmallCommands.showOnlineCount());
        NpcHtmlMessage msg = new NpcHtmlMessage(0);
        msg.setHtml(html);
        player.sendPacket(msg);
    }

    private static Optional<String> getSecondaryPass(Player player) {
        Optional<String> secondaryPassword = Optional.empty();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT secondaryPassword FROM accounts WHERE login='" + player.getAccountName() + "'");
             ResultSet rset = statement.executeQuery()) {
            if (rset.next()) {
                secondaryPassword = Optional.ofNullable(rset.getString("secondaryPassword"));
            }

        } catch (SQLException e) {
            _log.error("Error in secondary password:" + e);
        }
        return secondaryPassword;
    }
}
