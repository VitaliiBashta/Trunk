package l2trunk.loginserver.gameservercon;

import l2trunk.commons.net.AdvIP;
import l2trunk.loginserver.Config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameServer {
    private int _id;

    private String _internalHost, _externalHost;
    private int[] _ports = new int[]{7777};

    private int _serverType;
    private int _ageLimit;
    private int _protocol;
    private boolean _isOnline;
    private boolean _isPvp;
    private boolean _isShowingBrackets;
    private boolean _isGmOnly;

    private int _maxPlayers;

    private GameServerConnection _conn;
    private boolean _isAuthed;
    private int _port;
    private ArrayList<AdvIP> _ips;

    private final Set<String> _accounts = new CopyOnWriteArraySet<>();

    public GameServer(GameServerConnection conn) {
        _conn = conn;
    }

    public GameServer(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public boolean isAuthed() {
        return _isAuthed;
    }

    public void setAuthed(boolean isAuthed) {
        _isAuthed = isAuthed;
    }

    public GameServerConnection getConnection() {
        return _conn;
    }

    private void setConnection(GameServerConnection conn) {
        _conn = conn;
    }

    public InetAddress getInternalHost() throws UnknownHostException {
        return InetAddress.getByName(_internalHost);
    }

    public void setInternalHost(String internalHost) {
        if (internalHost.equals("*"))
            internalHost = getConnection().getIpAddress();

        _internalHost = internalHost;
    }

    public InetAddress getExternalHost() throws UnknownHostException {
        return InetAddress.getByName(_externalHost);
    }

    public void setExternalHost(String externalHost) {
        if (externalHost.equals("*"))
            externalHost = getConnection().getIpAddress();

        _externalHost = externalHost;
    }

    public int getPort() {
        return _ports[_port++ & _ports.length - 1];
    }

    public void setPorts(int[] ports) {
        _ports = ports;
    }

    public int getMaxPlayers() {
        return _maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        _maxPlayers = maxPlayers;
    }

    public int getOnline() {
        return _accounts.size();
    }

    public Set<String> getAccounts() {
        return _accounts;
    }

    public void addAccount(String account) {
        _accounts.add(account);
    }

    public void removeAccount(String account) {
        _accounts.remove(account);
    }

    public void setDown() {
        setAuthed(false);
        setConnection(null);
        setOnline(false);

        _accounts.clear();
    }

    public String getName() {
        return Config.SERVER_NAMES.get(getId());
    }

    public void sendPacket(SendablePacket packet) {
        GameServerConnection conn = getConnection();
        if (conn != null)
            conn.sendPacket(packet);
    }

    public int getServerType() {
        return _serverType;
    }

    public void setServerType(int serverType) {
        _serverType = serverType;
    }

    public boolean isOnline() {
        return _isOnline;
    }

    public void setOnline(boolean online) {
        _isOnline = online;
    }

    public boolean isPvp() {
        return _isPvp;
    }

    public void setPvp(boolean pvp) {
        _isPvp = pvp;
    }

    public boolean isShowingBrackets() {
        return _isShowingBrackets;
    }

    public void setShowingBrackets(boolean showingBrackets) {
        _isShowingBrackets = showingBrackets;
    }

    public boolean isGmOnly() {
        return _isGmOnly;
    }

    public void setGmOnly(boolean gmOnly) {
        _isGmOnly = gmOnly;
    }

    public int getAgeLimit() {
        return _ageLimit;
    }

    public void setAgeLimit(int ageLimit) {
        _ageLimit = ageLimit;
    }

    public int getProtocol() {
        return _protocol;
    }

    public void setProtocol(int protocol) {
        _protocol = protocol;
    }

    public ArrayList<AdvIP> getAdvIP() {
        return _ips;
    }

    public void setAdvIP(ArrayList<AdvIP> paramArrayList) {
        _ips = paramArrayList;
    }
}