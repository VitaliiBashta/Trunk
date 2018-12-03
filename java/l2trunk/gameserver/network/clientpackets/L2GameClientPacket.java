package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.net.nio.impl.ReceivablePacket;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.BufferUnderflowException;
import java.util.List;

/**
 * Packets received by the game server from clients
 */
public abstract class L2GameClientPacket extends ReceivablePacket<GameClient> {
    private static final Logger _log = LoggerFactory.getLogger(L2GameClientPacket.class);

    @Override
    public final boolean read() {
        try {
            readImpl();
            return true;
        } catch (BufferUnderflowException e) {
            client.onPacketReadFail();
        } catch (RuntimeException e) {
            _log.error("Client: " + client + " - Failed reading: " + getType() + " - Server Version: ", e);
        }

        return false;
    }

    protected abstract void readImpl();

    @Override
    public final void run() {
        GameClient client = getClient();
        try {
            runImpl();
        } catch (RuntimeException e) {
            _log.error("Client: " + client + " - Failed running: " + getType() + " - Server Version: ", e);
        }
    }

    protected abstract void runImpl();

    String readS(int len) {
        String ret = readS();
        return ret.length() > len ? ret.substring(0, len) : ret;
    }

    void sendPacket(L2GameServerPacket packet) {
        getClient().sendPacket(packet);
    }

    void sendPacket(L2GameServerPacket... packets) {
        getClient().sendPacket(packets);
    }

    protected void sendPackets(List<L2GameServerPacket> packets) {
        getClient().sendPackets(packets);
    }

    String getType() {
        return "[C] " + getClass().getSimpleName();
    }
}