package l2trunk.gameserver.network.loginservercon.gspackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.GameServer;
import l2trunk.gameserver.network.loginservercon.SendablePacket;

public final class AuthRequest extends SendablePacket {
    protected void writeImpl() {
        writeC(0x00);
        writeD(GameServer.AUTH_SERVER_PROTOCOL);
        writeC(Config.REQUEST_ID);
        writeC(Config.ACCEPT_ALTERNATE_ID ? 0x01 : 0x00);
        writeD(Config.AUTH_SERVER_SERVER_TYPE);
        writeD(Config.AUTH_SERVER_AGE_LIMIT);
        writeC(Config.AUTH_SERVER_GM_ONLY ? 0x01 : 0x00);
        writeC(Config.AUTH_SERVER_BRACKETS ? 0x01 : 0x00);
        writeC(Config.AUTH_SERVER_IS_PVP ? 0x01 : 0x00);
        writeS(Config.EXTERNAL_HOSTNAME);
        writeS(Config.INTERNAL_HOSTNAME);

        //ports
        writeH(Config.GAME_PORT.size());
        Config.GAME_PORT.forEach(this::writeH);

        writeD(Config.MAXIMUM_ONLINE_USERS);

        // Sends channels info to login server.
        writeD(Config.GAMEIPS.size());
        Config.GAMEIPS.forEach(ip -> {
            writeD(ip.channelId);
            writeS(ip.channelAdress);
            writeD(ip.channelPort);
        });
    }
}
