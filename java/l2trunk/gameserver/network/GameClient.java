package l2trunk.gameserver.network;

import l2trunk.commons.net.nio.impl.MMOClient;
import l2trunk.commons.net.nio.impl.MMOConnection;
import l2trunk.gameserver.SecondaryPasswordAuth;
import l2trunk.gameserver.dao.CharacterDAO;
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
    public static final String NO_IP = "?.?.?.?";
    private static final Logger _log = LoggerFactory.getLogger(GameClient.class);
    public static boolean SESSION_OK = MMOClient.SESSION_OK;
    private static byte[] _keyClientEn = new byte[8];
    private final GameCrypt _crypt;
    private List<Integer> _charSlotMapping = new ArrayList<>();
    private GameClientState _state;
    private SecondaryPasswordAuth _secondaryAuth;
    private String _fileId = "";
    private int _systemVer = -1;
    private int _serverId;
    /**
     * Данные аккаунта
     */
    private String login;
    private Player _activeChar;
    private SessionKey _sessionKey;
    private String _ip = NO_IP;
    private int revision = 0;
    private boolean _gameGuardOk = false;
    private int _failedPackets = 0;
    private int _unknownPackets = 0;
    private int _instanceCount;
    private boolean _isProtected;

    public GameClient(MMOConnection<GameClient> con) {
        super(con);

        _state = GameClientState.CONNECTED;
        _crypt = new GameCrypt();
        if (con != null)
            _ip = con.getSocket().getInetAddress().getHostAddress();
    }

    public static byte[] getKeyClientEn() {
        return _keyClientEn;
    }

    public static void setKeyClientEn(byte[] key) {
        _keyClientEn = key;
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

        if ((_activeChar != null) && (_activeChar.objectId() == objId)) {
            _activeChar.setDeleteTimer(0);
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=0 WHERE obj_id=?")) {
            statement.setInt(1, objId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error on markRestoredChar ", e);
        }
    }

    public void markToDeleteChar(int charSlot) {
        int objId = getObjectIdForSlot(charSlot);
        if (objId < 0) {
            return;
        }

        if ((_activeChar != null) && (_activeChar.objectId() == objId)) {
            _activeChar.setDeleteTimer((int) (System.currentTimeMillis() / 1000));
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE characters SET deletetime=? WHERE obj_id=?")) {
            statement.setLong(1, (int) (System.currentTimeMillis() / 1000L));
            statement.setInt(2, objId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("error on markToDeleteChar:", e);
        }
    }

    public void deleteChar(int charslot) {
        // have to make sure active character must be nulled
        if (_activeChar != null) {
            return;
        }

        int objid = getObjectIdForSlot(charslot);
        if (objid == -1) {
            return;
        }

        CharacterDAO.deleteCharByObjId(objid);
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
                    oldClient.closeNow(false);
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
            _log.warn("could not restore obj_id: " + objectId + " in slot:" + charSlot);
        }

        return character;
    }

    public int getObjectIdForSlot(int charslot) {
        if ((charslot < 0) || (charslot >= _charSlotMapping.size())) {
            _log.warn(getLogin() + " tried to modify Character in slot " + charslot + " but no characters exits at that slot.");
            return -1;
        }
        return _charSlotMapping.get(charslot);
    }

    public int getSlotForObjectId(int objectId) {
        return _charSlotMapping.indexOf(objectId);
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public void setActiveChar(Player player) {
        _activeChar = player;
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
        _charSlotMapping = chars.stream()
                .map(CharSelectInfoPackage::getObjectId)
                .collect(Collectors.toList());
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    @Override
    public boolean encrypt(final ByteBuffer buf, final int size) {
        _crypt.encrypt(buf.array(), buf.position(), size);
        buf.position(buf.position() + size);
        return true;
    }

    @Override
    public boolean decrypt(ByteBuffer buf, int size) {
        return _crypt.decrypt(buf.array(), buf.position(), size);
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
        return _ip;
    }

    public byte[] enableCrypt() {
        byte[] key = BlowFishKeygen.getRandomKey();
        _crypt.setKey(key);

        return key;
    }

    public byte[] getDecryptedProtocol(byte[] key) {
        _crypt.setKey(key);
        return key;
    }

    public GameClientState getState() {
        return _state;
    }

    public void setState(GameClientState state) {
        _state = state;
    }

    public void onPacketReadFail() {
        if (_failedPackets++ >= 10) {
            _log.warn("Too many client packet fails, connection closed : " + this);
            closeNow(true);
        }
    }

    void onUnknownPacket() {
        if (_unknownPackets++ >= 10) {
            _log.warn("Too many client unknown packets, connection closed : " + this);
            closeNow(true);
        }
    }

    @Override
    public String toString() {
        return _state + " IP: " + getIpAddr() + (login == null ? "" : " Account: " + login) + (_activeChar == null ? "" : " Player : " + _activeChar);
    }

    public SecondaryPasswordAuth getSecondaryAuth() {
        return _secondaryAuth;
    }

    public boolean isGameGuardOk() {
        return _gameGuardOk;
    }

    public void setGameGuardOk(boolean gameGuardOk) {
        _gameGuardOk = gameGuardOk;
    }


    public String getFileId() {
        return _fileId;
    }

    public void setFileId(String fileId) {
        this._fileId = fileId;
    }

    public int getSystemVer() {
        return _systemVer;
    }


    public void setSystemVersion(int ver) {
        _systemVer = ver;
    }

    public int getServerId() {
        return _serverId;
    }

    public void setServerId(int serverId) {
        _serverId = serverId;
    }

    public enum GameClientState {
        CONNECTED,
        AUTHED,
        IN_GAME,
        DISCONNECTED
    }
}