package l2trunk.commons.net.nio.impl;

import java.nio.channels.SocketChannel;

public interface IAcceptFilter {
    boolean accept(SocketChannel sc);
}
