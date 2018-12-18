package l2trunk.loginserver.accounts;

import l2trunk.commons.lang.Pair;
import l2trunk.commons.net.utils.Net;
import l2trunk.commons.net.utils.NetList;
import l2trunk.loginserver.database.L2DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class Account {
    private final static Logger _log = LoggerFactory.getLogger(Account.class);

    private final String login;
    private final NetList allowedIpList = new NetList();
    private final Map<Integer, Pair<Integer, int[]>> _serversInfo = new HashMap<>(2);
    private String passwordHash;
    private String allowedIP;
    private String allowedHwid;
    private int accessLevel;
    private int banExpire;
    private double bonus;
    private int bonusExpire;
    private String lastIP;
    private int lastAccess;
    private int lastServer;

    public Account(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    private String getAllowedIP() {
        return this.allowedIP;
    }

    public void setAllowedIP(String allowedIP) {
        if (allowedIP == null)
            return;
        allowedIpList.clear();
        this.allowedIP = allowedIP;

        if (this.allowedIP.isEmpty())
            return;

        String[] masks = this.allowedIP.split("[\\s,;]+");
        for (String mask : masks)
            this.allowedIpList.add(Net.valueOf(mask));
    }

    public String getAllowedHwid() {
        return allowedHwid;
    }

    public void setAllowedHwid(String allowedHwid) {
        this.allowedHwid = allowedHwid;
    }

    public boolean isAllowedIP(String ip) {
        return allowedIpList.isEmpty() || allowedIpList.isInRange(ip);
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public int getBonusExpire() {
        return bonusExpire;
    }

    public void setBonusExpire(int bonusExpire) {
        this.bonusExpire = bonusExpire;
    }

    public int getBanExpire() {
        return banExpire;
    }

    public void setBanExpire(int banExpire) {
        this.banExpire = banExpire;
    }

    public String getLastIP() {
        return lastIP;
    }

    public void setLastIP(String lastIP) {
        this.lastIP = lastIP;
    }

    private int getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(int lastAccess) {
        this.lastAccess = lastAccess;
    }

    public int getLastServer() {
        return lastServer;
    }

    public void setLastServer(int lastServer) {
        this.lastServer = lastServer;
    }

    public void addAccountInfo(int serverId, int size, int[] deleteChars) {
        _serversInfo.put(serverId, new Pair<>(size, deleteChars));
    }

    public Pair<Integer, int[]> getAccountInfo(int serverId) {
        return _serversInfo.get(serverId);
    }

    @Override
    public String toString() {
        return login;
    }

    public void restore() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT password, access_level, ban_expire, allow_ip, allow_hwid, bonus, bonus_expire, last_server, last_ip, last_access FROM accounts WHERE login = ?")) {
            statement.setString(1, login);
            ResultSet rset = statement.executeQuery();

            if (rset.next()) {
                setPasswordHash(rset.getString("password"));
                setAccessLevel(rset.getInt("access_level"));
                setBanExpire(rset.getInt("ban_expire"));
                setAllowedIP(rset.getString("allow_ip"));
                setAllowedHwid(rset.getString("allow_hwid"));
                setBonus(rset.getDouble("bonus"));
                setBonusExpire(rset.getInt("bonus_expire"));
                setLastServer(rset.getInt("last_server"));
                setLastIP(rset.getString("last_ip"));
                setLastAccess(rset.getInt("last_access"));
            }
        } catch (SQLException e) {
            _log.error("", e);
        }
    }

    public void save() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO accounts (login, password) VALUES(?,?)")) {
            statement.setString(1, getLogin());
            statement.setString(2, getPasswordHash());
            statement.execute();
        } catch (SQLException e) {
            _log.error("", e);
        }
    }

    public void update() {
        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE accounts SET password = ?, access_level = ?, ban_expire = ?, allow_ip = ?, allow_hwid=?, bonus = ?, bonus_expire = ?, last_server = ?, last_ip = ?, last_access = ? WHERE login = ?")) {
            statement.setString(1, getPasswordHash());
            statement.setInt(2, getAccessLevel());
            statement.setInt(3, getBanExpire());
            statement.setString(4, getAllowedIP());
            statement.setString(5, getAllowedHwid());
            statement.setDouble(6, getBonus());
            statement.setInt(7, getBonusExpire());
            statement.setInt(8, getLastServer());
            statement.setString(9, getLastIP());
            statement.setInt(10, getLastAccess());
            statement.setString(11, getLogin());
            statement.execute();
        } catch (SQLException e) {
            _log.error("", e);
        }
    }
}
