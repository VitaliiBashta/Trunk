package l2f.commons.net.nio.impl;

import java.nio.ByteBuffer;

@SuppressWarnings("rawtypes")
public interface IPacketHandler<T extends MMOClient> {
    ReceivablePacket<T> handlePacket(ByteBuffer buf, T client);
}