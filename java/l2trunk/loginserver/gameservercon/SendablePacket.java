package l2trunk.loginserver.gameservercon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public abstract class SendablePacket extends l2trunk.commons.net.nio.SendablePacket<GameServer> {
    private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);

    private GameServer _gs;
    private ByteBuffer _buf;

    @Override
    protected ByteBuffer getByteBuffer() {
        return _buf;
    }

    void setByteBuffer(ByteBuffer buf) {
        _buf = buf;
    }

    @Override
    public GameServer getClient() {
        return _gs;
    }

    void setClient(GameServer gs) {
        _gs = gs;
    }

    public GameServer getGameServer() {
        return getClient();
    }

    @Override
    public boolean write() {
        try {
            writeImpl();
        } catch (Exception e) {
            _log.error("", e);
        }
        return true;
    }

    protected abstract void writeImpl();
}
