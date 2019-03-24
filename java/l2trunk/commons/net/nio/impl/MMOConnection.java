package l2trunk.commons.net.nio.impl;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MMOConnection<T extends MMOClient> {
    private final SelectorThread<T> _selectorThread;

    private final SelectionKey _selectionKey;
    private final Socket socket;
    private final WritableByteChannel _writableByteChannel;
    private final ReadableByteChannel _readableByteChannel;

    private final Queue<SendablePacket<T>> sendQueue;
    private final Queue<ReceivablePacket<T>> _recvQueue;

    private T _client;
    private ByteBuffer _readBuffer, _primaryWriteBuffer, _secondaryWriteBuffer;

    private boolean pendingClose;
    private long pendingCloseTime;
    private boolean closed;

    private long _pendingWriteTime;
    private final AtomicBoolean _isPengingWrite = new AtomicBoolean();

    MMOConnection(SelectorThread<T> selectorThread, Socket socket, SelectionKey key) {
        _selectorThread = selectorThread;
        _selectionKey = key;
        this.socket = socket;
        _writableByteChannel = socket.getChannel();
        _readableByteChannel = socket.getChannel();
        sendQueue = new ArrayDeque<>();
        _recvQueue = new MMOExecutableQueue<>(selectorThread.getExecutor());
    }

    public T getClient() {
        return _client;
    }

    void setClient(T client) {
        _client = client;
    }

    void recvPacket(ReceivablePacket<T> rp) {
        if (rp == null)
            return;

        if (isClosed())
            return;

        _recvQueue.add(rp);
    }

    public void sendPacket(SendablePacket<T> sp) {
        if (sp == null)
            return;

        synchronized (this) {
            if (isClosed())
                return;

            sendQueue.add(sp);
        }

        scheduleWriteInterest();
    }

    @SuppressWarnings("unchecked")
    public void sendPacket(SendablePacket<T>... args) {
        if (args == null || args.length == 0)
            return;

        synchronized (this) {
            if (isClosed())
                return;

            for (SendablePacket<T> sp : args)
                if (sp != null)
                    sendQueue.add(sp);
        }

        scheduleWriteInterest();
    }

    public void sendPackets(List<? extends SendablePacket<T>> args) {
        if (args == null || args.isEmpty())
            return;

        SendablePacket<T> sp;

        synchronized (this) {
            if (isClosed())
                return;

            for (SendablePacket<T> arg : args)
                if ((sp = arg) != null)
                    sendQueue.add(sp);
        }

        scheduleWriteInterest();
    }

    SelectionKey getSelectionKey() {
        return _selectionKey;
    }

    private void disableReadInterest() {
        try {
            _selectionKey.interestOps(_selectionKey.interestOps() & ~SelectionKey.OP_READ);
        } catch (CancelledKeyException ignored) {
        }
    }

    void scheduleWriteInterest() {
        try {
            if (_isPengingWrite.compareAndSet(false, true))
                _pendingWriteTime = System.currentTimeMillis();
        } catch (CancelledKeyException e) {
            // ignore
        }
    }

    private void disableWriteInterest() {
        try {
            if (_isPengingWrite.compareAndSet(true, false))
                _selectionKey.interestOps(_selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
        } catch (CancelledKeyException e) {
            // ignore
        }
    }

    void enableWriteInterest() {
        if (_isPengingWrite.compareAndSet(true, false))
            _selectionKey.interestOps(_selectionKey.interestOps() | SelectionKey.OP_WRITE);
    }

    boolean isPendingWrite() {
        return _isPengingWrite.get();
    }

    public long getPendingWriteTime() {
        return _pendingWriteTime;
    }

    public Socket getSocket() {
        return socket;
    }

    public WritableByteChannel getWritableChannel() {
        return _writableByteChannel;
    }

    public ReadableByteChannel getReadableByteChannel() {
        return _readableByteChannel;
    }

    Queue<SendablePacket<T>> getSendQueue() {
        return sendQueue;
    }

    protected Queue<ReceivablePacket<T>> getRecvQueue() {
        return _recvQueue;
    }

    void createWriteBuffer(ByteBuffer buf) {
        if (_primaryWriteBuffer == null) {
            _primaryWriteBuffer = _selectorThread.getPooledBuffer();
            _primaryWriteBuffer.put(buf);
        } else {
            ByteBuffer temp = _selectorThread.getPooledBuffer();
            temp.put(buf);

            int remaining = temp.remaining();
            _primaryWriteBuffer.flip();
            int limit = _primaryWriteBuffer.limit();

            if (remaining >= _primaryWriteBuffer.remaining()) {
                temp.put(_primaryWriteBuffer);
                _selectorThread.recycleBuffer(_primaryWriteBuffer);
                _primaryWriteBuffer = temp;
            } else {
                _primaryWriteBuffer.limit(remaining);
                temp.put(_primaryWriteBuffer);
                _primaryWriteBuffer.limit(limit);
                _primaryWriteBuffer.compact();
                _secondaryWriteBuffer = _primaryWriteBuffer;
                _primaryWriteBuffer = temp;
            }
        }
    }

    boolean hasPendingWriteBuffer() {
        return _primaryWriteBuffer != null;
    }

    void movePendingWriteBufferTo(ByteBuffer dest) {
        _primaryWriteBuffer.flip();
        dest.put(_primaryWriteBuffer);
        _selectorThread.recycleBuffer(_primaryWriteBuffer);
        _primaryWriteBuffer = _secondaryWriteBuffer;
        _secondaryWriteBuffer = null;
    }

    public ByteBuffer getReadBuffer() {
        return _readBuffer;
    }

    void setReadBuffer(ByteBuffer buf) {
        _readBuffer = buf;
    }

    public boolean isClosed() {
        return pendingClose || closed;
    }

    public boolean isPengingClose() {
        return pendingClose;
    }

    public long getPendingCloseTime() {
        return pendingCloseTime;
    }

    void close() throws IOException {
        closed = true;
        socket.close();
    }

    void closeNow() {
        synchronized (this) {
            if (isClosed())
                return;

            sendQueue.clear();

            pendingClose = true;
            pendingCloseTime = System.currentTimeMillis();
        }

        disableReadInterest();
        disableWriteInterest();
    }

    public void close(SendablePacket<T> sp) {
        synchronized (this) {
            if (isClosed())
                return;

            sendQueue.clear();

            sendPacket(sp);

            pendingClose = true;
            pendingCloseTime = System.currentTimeMillis();
        }

        disableReadInterest();
    }

    void closeLater() {
        synchronized (this) {
            if (isClosed())
                return;

            pendingClose = true;
            pendingCloseTime = System.currentTimeMillis();
        }
    }

    void releaseBuffers() {
        if (_primaryWriteBuffer != null) {
            _selectorThread.recycleBuffer(_primaryWriteBuffer);
            _primaryWriteBuffer = null;
            if (_secondaryWriteBuffer != null) {
                _selectorThread.recycleBuffer(_secondaryWriteBuffer);
                _secondaryWriteBuffer = null;
            }
        }
        if (_readBuffer != null) {
            _selectorThread.recycleBuffer(_readBuffer);
            _readBuffer = null;
        }
    }

    void clearQueues() {
        sendQueue.clear();
        _recvQueue.clear();
    }

    void onDisconnection() {
        getClient().onDisconnection();
    }

    void onForcedDisconnection() {
        getClient().onForcedDisconnection();
    }

    @Override
    public String toString() {
        return "MMOConnection: selector=" + _selectorThread + "; client=" + getClient();
    }
}