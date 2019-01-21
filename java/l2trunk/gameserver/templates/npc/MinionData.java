package l2trunk.gameserver.templates.npc;

public final class MinionData {
    private final int minionId;
    private final int minionAmount;

    public MinionData(int minionId, int minionAmount) {
        this.minionId = minionId;
        this.minionAmount = minionAmount;
    }

    public int getMinionId() {
        return minionId;
    }

    public int getAmount() {
        return minionAmount;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o == null)
            return false;
        if (o.getClass() != this.getClass())
            return false;
        return ((MinionData) o).getMinionId() == this.getMinionId();
    }
}