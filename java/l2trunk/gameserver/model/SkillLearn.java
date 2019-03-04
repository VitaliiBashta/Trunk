package l2trunk.gameserver.model;

public final class SkillLearn implements Comparable<SkillLearn> {
    public final int id;
    public final int level;
    public final int minLevel;
    public final int cost;
    public final int itemId;
    public final long itemCount;
    public final boolean isClicked;

    public SkillLearn(int id, int level, int minLevel, int cost, int itemId, long itemCount, boolean isClicked) {
        this.id = id;
        this.level = level;
        this.minLevel = minLevel;
        this.cost = cost;
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.isClicked = isClicked;
    }


    @Override
    public int compareTo(SkillLearn o) {
        if (id == o.id)
            return level - o.level;
        else
            return id - o.id;
    }
}