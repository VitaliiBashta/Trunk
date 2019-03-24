package l2trunk.loginserver;

import l2trunk.commons.net.nio.impl.MMOClient;
import l2trunk.commons.net.nio.impl.MMOConnection;
import l2trunk.loginserver.accounts.Account;
import l2trunk.loginserver.crypt.LoginCrypt;
import l2trunk.loginserver.crypt.ScrambledKeyPair;
import l2trunk.loginserver.serverpackets.AccountKicked;
import l2trunk.loginserver.serverpackets.AccountKicked.AccountKickedReason;
import l2trunk.loginserver.serverpackets.L2LoginServerPacket;
import l2trunk.loginserver.serverpackets.LoginFail;
import l2trunk.loginserver.serverpackets.LoginFail.LoginFailReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPrivateKey;

public final class L2LoginClient extends MMOClient<MMOConnection<L2LoginClient>> {
    private final static Logger LOG = LoggerFactory.getLogger(L2LoginClient.class);
    private final String ipAddr;
    private LoginClientState state;
    private LoginCrypt logincrypt;
    private ScrambledKeyPair scrambledPair;
    private byte[] blowfishKey;
    private String login;
    private SessionKey sessionKey;
    private Account account;
    private int sessionId;

    public L2LoginClient(MMOConnection<L2LoginClient> con) {
        super(con);
        state = LoginClientState.CONNECTED;
        scrambledPair = Config.getScrambledRSAKeyPair();
        blowfishKey = Config.getBlowfishKey();
        logincrypt = new LoginCrypt();
        logincrypt.setKey(blowfishKey);
        sessionId = con.hashCode();
        ipAddr = getConnection().getSocket().getInetAddress().getHostAddress();
    }

    @Override
    public boolean decrypt(ByteBuffer buf, int size) {
        boolean ret;
        try {
            ret = logincrypt.decrypt(buf.array(), buf.position(), size);
        } catch (IOException e) {
            LOG.error("", e);
            closeNow();
            return false;
        }

        if (!ret)
            closeNow();
        return ret;
    }

    @Override
    public void encrypt(ByteBuffer buf, int size) {
        final int offset = buf.position();
        try {
            size = logincrypt.encrypt(buf.array(), offset, size);
        } catch (IOException e) {
            LOG.error("", e);
            return;
        }

        buf.position(offset + size);
    }

    public LoginClientState getState() {
        return state;
    }

    public void setState(LoginClientState state) {
        this.state = state;
    }

    public byte[] getBlowfishKey() {
        return blowfishKey;
    }

    public byte[] getScrambledModulus() {
        return scrambledPair.getScrambledModulus();
    }

    public RSAPrivateKey getRSAPrivateKey() {
        return (RSAPrivateKey) scrambledPair.getKeyPair().getPrivate();
    }

    private String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public SessionKey getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(SessionKey skey) {
        this.sessionKey = skey;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int val) {
        sessionId = val;
    }

    public void sendPacket(L2LoginServerPacket lsp) {
        if (isConnected())
            getConnection().sendPacket(lsp);
    }

    public void close(LoginFailReason reason) {
        if (isConnected())
            getConnection().close(new LoginFail(reason));
    }

    public void close(AccountKickedReason reason) {
        if (isConnected())
            getConnection().close(new AccountKicked(reason));
    }

    public void close(L2LoginServerPacket lsp) {
        if (isConnected())
            getConnection().close(lsp);
    }

    @Override
    public void onDisconnection() {
        state = LoginClientState.DISCONNECTED;
        sessionKey = null;
        logincrypt = null;
        scrambledPair = null;
        blowfishKey = null;
    }

    @Override
    public String toString() {
        if (state == LoginClientState.AUTHED)
            return "[ Account : " + getLogin() + " IP: " + getIpAddress() + "]";
        return "[ State : " + getState() + " IP: " + getIpAddress() + "]";
    }

    public String getIpAddress() {
        return ipAddr;
    }

    @Override
    protected void onForcedDisconnection() {

    }

    public enum LoginClientState {
        CONNECTED,
        AUTHED_GG,
        AUTHED,
        DISCONNECTED,
        FAKE_LOGIN
    }
}