package l2trunk.commons.net.nio.impl;

import java.nio.ByteBuffer;

public abstract class MMOClient<T extends MMOConnection> {
    public static final boolean SESSION_OK = false;
    private T connection;
    private boolean isAuthed;

    protected MMOClient(T con) {
        connection = con;
    }

    public T getConnection() {
        return connection;
    }

    void setConnection(T con) {
        connection = con;
    }

    public boolean isAuthed() {
        return isAuthed;
    }

    public void setAuthed(boolean isAuthed) {
        this.isAuthed = isAuthed;
    }

    public void closeNow() {
        if (isConnected())
            connection.closeNow();
    }

    public void closeLater() {
        if (isConnected())
            connection.closeLater();
    }

    public boolean isConnected() {
        return connection != null && !connection.isClosed();
    }

    public abstract boolean decrypt(ByteBuffer buf, int size);

    public abstract void encrypt(ByteBuffer buf, int size);

    protected void onDisconnection() {
    }

    protected void onForcedDisconnection() {
    }
}