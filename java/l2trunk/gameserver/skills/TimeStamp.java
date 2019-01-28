package l2trunk.gameserver.skills;

import l2trunk.gameserver.model.Skill;

public final class TimeStamp {
    public final int id;
    public final int level;
    private final long reuse;
    private final long endTime;

    public TimeStamp(int id, long endTime, long reuse) {
        this.id = id;
        level = 0;
        this.reuse = reuse;
        this.endTime = endTime;
    }

    public TimeStamp(Skill skill, long reuse) {
        this(skill, System.currentTimeMillis() + reuse, reuse);
    }

    public TimeStamp(Skill skill, long endTime, long reuse) {
        id = skill.id;
        level = skill.level;
        this.reuse = reuse;
        this.endTime = endTime;
    }

    public long getReuseBasic() {
        if (reuse == 0)
            return getReuseCurrent();
        return reuse;
    }

    public long getReuseCurrent() {
        return Math.max(endTime - System.currentTimeMillis(), 0);
    }

    public long endTime() {
        return endTime;
    }

    public boolean hasNotPassed() {
        return System.currentTimeMillis() < endTime;
    }

    public int id() {
        return id;
    }

}