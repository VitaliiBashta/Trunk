package l2trunk.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class SendablePacket<T extends MMOClient> extends l2trunk.commons.net.nio.SendablePacket<T> {
    @Override
    protected ByteBuffer getByteBuffer() {
        return ((SelectorThread) Thread.currentThread()).getWriteBuffer();
    }

    @Override
    public T getClient() {
        return (T) ((SelectorThread) Thread.currentThread()).getWriteClient();
    }

    protected abstract boolean write();
}