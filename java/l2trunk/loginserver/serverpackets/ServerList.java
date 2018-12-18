package l2trunk.loginserver.serverpackets;

import l2trunk.commons.lang.Pair;
import l2trunk.commons.net.AdvIP;
import l2trunk.commons.net.utils.NetUtils;
import l2trunk.loginserver.GameServerManager;
import l2trunk.loginserver.accounts.Account;
import l2trunk.loginserver.gameservercon.GameServer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

public final class ServerList extends L2LoginServerPacket {
    private final Map<Integer, ServerData> _servers;

    private final int _lastServer;

    public ServerList(Account account) {
        _servers = new TreeMap<>();

        _lastServer = account.getLastServer();

        for (GameServer gs : GameServerManager.getInstance().getGameServers()) {
            InetAddress ip;
            try {
                ip = NetUtils.isInternalIP(account.getLastIP()) ? gs.getInternalHost() : gs.getExternalHost();
            } catch (UnknownHostException e) {
                continue;
            }

            // Adds original server.
            addServer(gs.getId(), ip, gs.getPort(), gs, account);

            // Adds channels
            if (gs.getAdvIP() != null) {
                for (AdvIP localAdvIP : gs.getAdvIP()) {
                    try {
                        addServer(localAdvIP.channelId, InetAddress.getByName(localAdvIP.channelAdress), localAdvIP.channelPort, gs, account);

                        //_log.warn("Adding server: " + localAdvIP.channelAdress + ":" + localAdvIP.channelPort + ", id: " + localAdvIP.channelId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void addServer(int server_id, InetAddress ip, int port, final GameServer gs, Account account) {
        Pair<Integer, int[]> entry = account.getAccountInfo(gs.getId());

        try {
            _servers.put(server_id, new ServerData(ip, port, gs.isPvp(), gs.isShowingBrackets(), gs.getServerType(), gs.getOnline(), gs.getMaxPlayers(), gs.isOnline(), entry == null ? 0 : entry.getKey(), gs.getAgeLimit(), entry == null ? new int[0] : entry.getValue(), server_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void writeImpl() {
        writeC(0x04);
        writeC(_servers.size());
        writeC(_lastServer);

        ServerData server;

        for (Integer serverId : _servers.keySet()) {
            server = _servers.get(serverId);
            writeC(server.serverId);

            InetAddress i4 = server.ip;
            byte[] raw = i4.getAddress();
            writeC(raw[0] & 0xff);
            writeC(raw[1] & 0xff);
            writeC(raw[2] & 0xff);
            writeC(raw[3] & 0xff);

            writeD(server.port);
            writeC(server.ageLimit); // age limit
            writeC(server.pvp ? 0x01 : 0x00);
            writeH(server.online);
            writeH(server.maxPlayers);
            writeC(server.status ? 0x01 : 0x00);
            writeD(server.type);
            writeC(server.brackets ? 0x01 : 0x00);
        }

        writeH(0x00); // -??
        writeC(_servers.size());

        for (Integer serverId : _servers.keySet()) {
            server = _servers.get(serverId);
            writeC(server.serverId);
            writeC(server.playerSize); // acc player size
            writeC(server.deleteChars.length);
            for (int t : server.deleteChars)
                writeD((int) (t - System.currentTimeMillis() / 1000L));
        }
    }

    private static class ServerData {
        final InetAddress ip;
        final int port;
        final int online;
        final int maxPlayers;
        final boolean status;
        final boolean pvp;
        final boolean brackets;
        final int type;
        final int ageLimit;
        final int playerSize;
        final int[] deleteChars;
        final int serverId;

        ServerData(InetAddress ip, int port, boolean pvp, boolean brackets, int type, int online, int maxPlayers, boolean status, int size, int ageLimit, int[] d, int serverId) {
            this.ip = ip;
            this.port = port;
            this.pvp = pvp;
            this.brackets = brackets;
            this.type = type;
            this.online = online;
            this.maxPlayers = maxPlayers;
            this.status = status;
            this.playerSize = size;
            this.ageLimit = ageLimit;
            this.deleteChars = d;
            this.serverId = serverId;
        }
    }
}