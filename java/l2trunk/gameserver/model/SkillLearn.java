package l2trunk.gameserver.model;

public final class SkillLearn implements Comparable<SkillLearn> {
    public final int id;
    public final int level;
    private final int minLevel;
    private final int cost;
    private final int itemId;
    private final long itemCount;
    private final boolean clicked;

    public SkillLearn(int id, int lvl, int minLvl, int cost, int itemId, long itemCount, boolean clicked) {
        this.id = id;
        level = lvl;
        minLevel = minLvl;
        this.cost = cost;

        this.itemId = itemId;
        this.itemCount = itemCount;
        this.clicked = clicked;
    }

    public int id() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getCost() {
        return cost;
    }

    public int getItemId() {
        return itemId;
    }

    public long getItemCount() {
        return itemCount;
    }

    public boolean isClicked() {
        return clicked;
    }

    @Override
    public int compareTo(SkillLearn o) {
        if (id == o.id)
            return level - o.level;
        else
            return id - o.id;
    }
}