package l2trunk.gameserver.network.loginservercon;

import l2trunk.gameserver.network.loginservercon.lspackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public final class PacketHandler {
    private static final Logger _log = LoggerFactory.getLogger(PacketHandler.class);

    public static ReceivablePacket handlePacket(ByteBuffer buf) {
        int id = buf.get() & 0xff;

        switch (id) {
            case 0x00:
                return new AuthResponse();
            case 0x01:
                return new LoginServerFail();
            case 0x02:
                return new PlayerAuthResponse();
            case 0x03:
                return new KickPlayer();
            case 0x04:
                return new GetAccountInfo();
            case 0x06:
                return new ChangePasswordResponse();
            case 0x07:
                return new GetNewIds();
            case 0xff:
                return new PingRequest();
            default:
                _log.error("Received unknown packet: " + Integer.toHexString(id));
                return null;
        }
    }
}
