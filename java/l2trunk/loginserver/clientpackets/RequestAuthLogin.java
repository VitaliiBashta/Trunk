package l2trunk.loginserver.clientpackets;

import l2trunk.loginserver.Config;
import l2trunk.loginserver.GameServerManager;
import l2trunk.loginserver.IpBanManager;
import l2trunk.loginserver.L2LoginClient;
import l2trunk.loginserver.L2LoginClient.LoginClientState;
import l2trunk.loginserver.accounts.Account;
import l2trunk.loginserver.accounts.SessionManager;
import l2trunk.loginserver.accounts.SessionManager.Session;
import l2trunk.loginserver.crypt.PasswordHash;
import l2trunk.loginserver.gameservercon.GameServer;
import l2trunk.loginserver.gameservercon.lspackets.GetAccountInfo;
import l2trunk.loginserver.serverpackets.LoginFail.LoginFailReason;
import l2trunk.loginserver.serverpackets.LoginOk;

import javax.crypto.Cipher;


/**
 * Format: buffPrice[128]ddddddhc
 * buffPrice[128]: the rsa encrypted setBlock with the login an password
 */
public class RequestAuthLogin extends L2LoginClientPacket {
    private final byte[] _raw = new byte[128];

    private static void afterConnection(Account account, String passwordHash, String password, L2LoginClient client, String user) {
        boolean passwordCorrect = account.getPasswordHash().equals(passwordHash);
        int currentTime = (int) (System.currentTimeMillis() / 1000L);

        if (!passwordCorrect) {
            // check if the password is not encrypted by one of the older but supported algorithms
            for (PasswordHash c : Config.LEGACY_CRYPT)
                if (c.compare(password, account.getPasswordHash())) {
                    passwordCorrect = true;
                    account.setPasswordHash(passwordHash);
                    break;
                }
        }
        if (password.equals(account.getPasswordHash()))
            passwordCorrect = true;

        if (!IpBanManager.getInstance().tryLogin(client.getIpAddress(), passwordCorrect)) {
            client.closeNow();
            return;
        }

        if (!passwordCorrect) {
            if (!Config.FAKE_LOGIN_SERVER) {
                client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
                return;
            }
        }

        if (account.getAccessLevel() < 0) {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }

        if (account.getBanExpire() > currentTime) {
            client.close(LoginFailReason.REASON_ACCESS_FAILED);
            return;
        }

        if (!account.isAllowedIP(client.getIpAddress())) {
            client.close(LoginFailReason.REASON_ATTEMPTED_RESTRICTED_IP);
            return;
        }

        for (GameServer gs : GameServerManager.getInstance().getGameServers())
            if (gs.getProtocol() >= 2 && gs.isAuthed())
                gs.sendPacket(new GetAccountInfo(user));

        account.setLastAccess(currentTime);
        account.setLastIP(client.getIpAddress());

        Session session = SessionManager.getInstance().openSession(account);

        client.setAuthed(true);
        client.setLogin(user);
        client.setAccount(account);
        client.setSessionKey(session.getSessionKey());

        if (Config.FAKE_LOGIN_SERVER && !passwordCorrect)
            client.setState(LoginClientState.FAKE_LOGIN);
        else
            client.setState(LoginClientState.AUTHED);

        client.sendPacket(new LoginOk(client.getSessionKey()));
        //IPUtils.updateAccountRegion(client, user);
    }

    @Override
    protected void readImpl() {
        readB(_raw);
        readD();
        readD();
        readD();
        readD();
        readD();
        readD();
        readH();
        readC();
    }

    @SuppressWarnings("unused")
    @Override
    protected void runImpl() {
        L2LoginClient client = getClient();

        byte[] decrypted;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, client.getRSAPrivateKey());
            decrypted = rsaCipher.doFinal(_raw, 0x00, 0x80);
        } catch (Exception e) {
            client.closeNow();
            return;
        }

        String user = new String(decrypted, 0x5E, 14).trim();
        user = user.toLowerCase();
        String password = new String(decrypted, 0x6C, 16).trim();
        int ncotp = decrypted[0x7c];
        ncotp |= decrypted[0x7d] << 8;
        ncotp |= decrypted[0x7e] << 16;
        ncotp |= decrypted[0x7f] << 24;

        int currentTime = (int) (System.currentTimeMillis() / 1000L);

        Account account = new Account(user);
        account.restore();

        if (account.getPasswordHash() == null) {
            boolean any1On = false;
            for (GameServer gs : GameServerManager.getInstance().getGameServers())
                if (gs.isAuthed())
                    any1On = true;

            if (!any1On)
                return;

            if ((Config.AUTO_CREATE_ACCOUNTS) && (user.matches(Config.ANAME_TEMPLATE)) && (password.matches(Config.APASSWD_TEMPLATE))) {
                account.setAllowedIP("");
                account.setAllowedHwid("");
                account.setPasswordHash(password);
                account.save();
                afterConnection(account, password, password, client, user);
                return;
            } else {
                client.close(LoginFailReason.REASON_USER_OR_PASS_WRONG);
                return;
            }
        }

        afterConnection(account, password, password, client, user);
    }
}