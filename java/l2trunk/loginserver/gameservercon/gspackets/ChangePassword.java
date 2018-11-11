package l2trunk.loginserver.gameservercon.gspackets;

import l2trunk.loginserver.database.L2DatabaseFactory;
import l2trunk.loginserver.gameservercon.ReceivablePacket;
import l2trunk.loginserver.gameservercon.lspackets.ChangePasswordResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public final  class ChangePassword extends ReceivablePacket {
    private static final Logger log = Logger.getLogger(ChangePassword.class.getName());
    private String oldPass;
    private String newPass;
    private String accname;


    @Override
    protected void readImpl() {
        accname = readS();
        oldPass = readS();
        newPass = readS();
        String hwid = readS();
    }

    @Override
    protected void runImpl() {
        String dbPassword = null;
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement select = con.prepareStatement("SELECT * FROM accounts WHERE login = ?");
             PreparedStatement update = con.prepareStatement("UPDATE accounts SET password = ? WHERE login = ?");) {
            select.setString(1, accname);
            ResultSet rs = select.executeQuery();
            if (rs.next())
                dbPassword = rs.getString("password");

            if (!oldPass.equals(dbPassword)) {
                ChangePasswordResponse cp1;
                cp1 = new ChangePasswordResponse(accname, false);
                sendPacket(cp1);
            } else {
                update.setString(1, newPass);
                update.setString(2, accname);
                int result = update.executeUpdate();

                ChangePasswordResponse cp1;
                cp1 = new ChangePasswordResponse(accname, result != 0);
                sendPacket(cp1);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
