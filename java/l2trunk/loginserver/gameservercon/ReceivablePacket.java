package l2trunk.loginserver.gameservercon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


public abstract class ReceivablePacket extends l2trunk.commons.net.nio.ReceivablePacket<GameServer> {
    private static final Logger _log = LoggerFactory.getLogger(ReceivablePacket.class);

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

    protected GameServer getGameServer() {
        return getClient();
    }

    @Override
    public final boolean read() {
        try {
            readImpl();
        } catch (Exception e) {
            _log.error("", e);
        }
        return true;
    }

    @Override
    public final void run() {
        try {
            runImpl();
        } catch (Exception e) {
            _log.error("", e);
        }
    }

    protected abstract void readImpl();

    protected abstract void runImpl();

    protected void sendPacket(SendablePacket packet) {
        getGameServer().sendPacket(packet);
    }
}