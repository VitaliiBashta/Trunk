package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.network.serverpackets.KeyPacket;
import l2trunk.gameserver.network.serverpackets.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProtocolVersion extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);

    private int protocol;

    protected void readImpl() {
        protocol = readD();

        if ((buf.remaining() > 260)) {
            buf.position(buf.position() + 260);
        }
    }

    protected void runImpl() {
        if (protocol == -2) {
            client.closeNow(false);
            return;
        } else if (protocol == -3) {
            _log.info("Status request from IP : " + getClient().getIpAddr());
            getClient().close(new SendStatus());
            return;
        } else if (protocol < Config.MIN_PROTOCOL_REVISION || protocol > Config.MAX_PROTOCOL_REVISION) {
            _log.warn("Unknown protocol revision : " + protocol + ", client : " + client);
            getClient().close(new KeyPacket(null));
            return;
        }

        client.setSystemVersion(Config.LATEST_SYSTEM_VER);

        sendPacket(new KeyPacket(client.enableCrypt()));
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}