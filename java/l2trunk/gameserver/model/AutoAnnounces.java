package l2trunk.gameserver.model;

import java.util.ArrayList;

public final class AutoAnnounces {
    private final int id;
    private ArrayList<String> msg;
    private int repeat;
    private long nextSend;

    public AutoAnnounces(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setAnnounce(int delay, int repeat, ArrayList<String> msg) {
        nextSend = System.currentTimeMillis() + delay * 1000;
        this.repeat = repeat;
        this.msg = msg;
    }

    public void updateRepeat() {
        nextSend = System.currentTimeMillis() + repeat * 1000;
    }

    public boolean canAnnounce() {
        return System.currentTimeMillis() > nextSend;
    }

    public ArrayList<String> getMessage() {
        return msg;
    }
}