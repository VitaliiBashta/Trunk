package l2trunk.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class ReceivablePacket<T extends MMOClient> extends l2trunk.commons.net.nio.ReceivablePacket<T> {
    protected T client;
    protected ByteBuffer buf;

    @Override
    protected ByteBuffer getByteBuffer() {
        return buf;
    }

    void setByteBuffer(ByteBuffer buf) {
        this.buf = buf;
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