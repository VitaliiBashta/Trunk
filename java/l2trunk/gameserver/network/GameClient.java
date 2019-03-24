package l2trunk.gameserver.network;

import l2trunk.commons.net.nio.impl.MMOClient;
import l2trunk.commons.net.nio.impl.MMOConnection;
import l2trunk.gameserver.SecondaryPasswordAuth;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.CharSelectInfoPackage;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.loginservercon.AuthServerCommunication;
import l2trunk.gameserver.network.loginservercon.SessionKey;
import l2trunk.gameserver.network.loginservercon.gspackets.PlayerLogout;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public final class GameClient extends MMOClient<MMOConnection<GameClient>> {
    private static final Logger LOG = LoggerFactory.getLogger(GameClient.class);
    private final GameCrypt crypt;
    private List<Integer> charSlotMapping = new ArrayList<>();
    private GameClientState state;
    private SecondaryPasswordAuth secondaryAuth;
    private int serverId;
    /**
     * Данные аккаунта
     */
    private String login;
    private Player activeChar;
    private SessionKey _sessionKey;
    private String ip = "?.?.?.?";
    private int failedPackets = 0;
    private int _unknownPackets = 0;

    public GameClient(MMOConnection<GameClient> con) {
        super(con);

        state = GameClientState.CONNECTED;
        crypt = new GameCrypt();
        if (con != null)
            ip = con.getSocket().getInetAddress().getHostAddress();
    }

    @Override
    protected void onDisconnection() {
        final Player player;

        setState(GameClientState.DISCONNECTED);
        player = getActiveChar();
        setActiveChar(null);

        if (player != null && player.getNetConnection() != null) {
            player.setNetConnection(null);
            player.scheduleDelete();
        }

        if (getSessionKey() != null) {
            if (isAuthed()) {
                AuthServerCommunication.getInstance().removeAuthedClient(getLogin());
                AuthServerCommunication.getInstance().sendPacket(new PlayerLogout(getLogin()));
            } else {
                AuthServerCommunication.getInstance().removeWaitingClient(getLogin());
            }
        }
    }

    @Override
    protected void onForcedDisconnection() {
        // TODO Auto-generated method stub

    }

    public void markRestoredChar(int charSlot) {
        int objId = getObjectIdForSlot(charSlot);
        if (objId < 0) {
            return;
        }

        if ((activeChar != null) && (activeChar.objectId() == objId)) {
            activeChar.setDeleteTimer(0);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?")) {
            statement.setInt(1, objId);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Error on markRestoredChar ", e);
        }
    }

    public void markToDeleteChar(int charSlot) {
        int objId = getObjectIdForSlot(charSlot);
        if (objId < 0) {
            return;
        }

        if ((activeChar != null) && (activeChar.objectId() == objId)) {
            activeChar.setDeleteTimer((int) (System.currentTimeMillis() / 1000));
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?")) {
            statement.setLong(1, (int) (System.currentTimeMillis() / 1000L));
            statement.setInt(2, objId);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("error on markToDeleteChar:", e);
        }
    }

    public Player loadCharFromDisk(int charSlot, int objectId) {
        if (objectId == -1) {
            return null;
        }

        Player character = null;
        Player oldPlayer = GameObjectsStorage.getPlayer(objectId);

        if (oldPlayer != null) {
            if (oldPlayer.isLogoutStarted()) {
                oldPlayer.kick();//Kicking Offline Shop Player
                return null;
            } else {
                oldPlayer.sendPacket(SystemMsg.ANOTHER_PERSON_HAS_LOGGED_IN_WITH_THE_SAME_ACCOUNT);
                //Kicking real player that was on the char
                GameClient oldClient = oldPlayer.getNetConnection();
                if (oldClient != null) {
                    oldClient.setActiveChar(null);
                    oldClient.closeNow();
                }
                oldPlayer.setNetConnection(this);
                character = oldPlayer;
            }
        }

        if (character == null) {
            character = Player.restore(objectId);
        }

        if (character != null) {
            setActiveChar(character);
        } else {
            LOG.warn("could not restore obj_id: " + objectId + " in slot:" + charSlot);
        }

        return character;
    }

    public int getObjectIdForSlot(int charslot) {
        if ((charslot < 0) || (charslot >= charSlotMapping.size())) {
            LOG.warn(getLogin() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
            return -1;
        }
        return charSlotMapping.get(charslot);
    }

    public Player getActiveChar() {
        return activeChar;
    }

    public void setActiveChar(Player player) {
        activeChar = player;
        if (player != null) {
            player.setNetConnection(this);
        }
    }

    /**
     * @return Returns the sessionId.
     */
    public SessionKey getSessionKey() {
        return _sessionKey;
    }

    public String getLogin() {
        return login;
    }

    public void setLoginName(String loginName) {
        login = loginName;
    }

    public void setSessionId(SessionKey sessionKey) {
        _sessionKey = sessionKey;
    }

    public void setCharSelection(List<CharSelectInfoPackage> chars) {
        charSlotMapping = chars.stream()
                .map(CharSelectInfoPackage::getObjectId)
                .collect(Collectors.toList());
    }

    @Override
    public void encrypt(final ByteBuffer buf, final int size) {
        crypt.encrypt(buf.array(), buf.position(), size);
        buf.position(buf.position() + size);
    }

    @Override
    public boolean decrypt(ByteBuffer buf, int size) {
        return crypt.decrypt(buf.array(), buf.position(), size);
    }

    public void sendPacket(L2GameServerPacket gsp) {
        if (isConnected()) {
            getConnection().sendPacket(gsp);
        }
    }

    public void sendPacket(L2GameServerPacket... gsp) {
        if (isConnected()) {
            getConnection().sendPacket(gsp);
        }
    }

    public void sendPackets(List<L2GameServerPacket> gsp) {
        if (isConnected()) {
            getConnection().sendPackets(gsp);
        }
    }

    public void close(L2GameServerPacket gsp) {
        if (isConnected()) {
            getConnection().close(gsp);
        }
    }

    public String getIpAddr() {
        return ip;
    }

    public byte[] enableCrypt() {
        byte[] key = BlowFishKeygen.getRandomKey();
        crypt.setKey(key);

        return key;
    }

    public GameClientState getState() {
        return state;
    }

    public void setState(GameClientState state) {
        this.state = state;
    }

    public void onPacketReadFail() {
        if (failedPackets++ >= 10) {
            LOG.warn("Too many client packet fails, connection closed : " + this);
            closeNow();
        }
    }

    void onUnknownPacket() {
        if (_unknownPackets++ >= 10) {
            LOG.warn("Too many client unknown packets, connection closed : " + this);
            closeNow();
        }
    }

    @Override
    public String toString() {
        return state + " IP: " + getIpAddr() + (login == null ? "" : " Account: " + login) + (activeChar == null ? "" : " Player : " + activeChar);
    }

    public SecondaryPasswordAuth getSecondaryAuth() {
        return secondaryAuth;
    }


    public int getServerId() {
        return serverId;
    }

    public enum GameClientState {
        CONNECTED,
        AUTHED,
        IN_GAME,
        DISCONNECTED
    }
}