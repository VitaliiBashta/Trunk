package l2trunk.gameserver.network.loginservercon;

import l2trunk.gameserver.network.loginservercon.lspackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

class PacketHandler {
    private static final Logger _log = LoggerFactory.getLogger(PacketHandler.class);

    public static ReceivablePacket handlePacket(ByteBuffer buf) {
        ReceivablePacket packet = null;

        int id = buf.get() & 0xff;

        switch (id) {
            case 0x00:
                packet = new AuthResponse();
                break;
            case 0x01:
                packet = new LoginServerFail();
                break;
            case 0x02:
                packet = new PlayerAuthResponse();
                break;
            case 0x03:
                packet = new KickPlayer();
                break;
            case 0x04:
                packet = new GetAccountInfo();
                break;
            case 0x06:
                packet = new ChangePasswordResponse();
                break;
            case 0x07:
                packet = new GetNewIds();
                break;
            case 0xff:
                packet = new PingRequest();
                break;
            default:
                _log.error("Received unknown packet: " + Integer.toHexString(id));
        }

        return packet;
    }
}
