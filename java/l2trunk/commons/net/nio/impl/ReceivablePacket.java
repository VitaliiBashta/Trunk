package l2trunk.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class ReceivablePacket<T extends MMOClient> extends l2trunk.commons.net.nio.ReceivablePacket<T> {
    protected T client;
    protected ByteBuffer _buf;

    @Override
    protected ByteBuffer getByteBuffer() {
        return _buf;
    }

    void setByteBuffer(ByteBuffer buf) {
        _buf = buf;
    }

    @Override
    public T getClient() {
        return client;
    }

    void setClient(T client) {
        this.client = client;
    }

    protected abstract boolean read();
}