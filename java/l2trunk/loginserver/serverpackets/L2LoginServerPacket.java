package l2trunk.loginserver.serverpackets;

import l2trunk.commons.net.nio.impl.SendablePacket;
import l2trunk.loginserver.L2LoginClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2LoginServerPacket extends SendablePacket<L2LoginClient> {
    private static final Logger LOG = LoggerFactory.getLogger(L2LoginServerPacket.class);

    @Override
    public final boolean write() {
        try {
            writeImpl();
            return true;
        } catch (Exception e) {
            LOG.error("Client: " + getClient() + " - Failed writing: " + getClass().getSimpleName() + "!", e);
        }
        return false;
    }

    protected abstract void writeImpl();
}
