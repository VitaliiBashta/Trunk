package l2trunk.gameserver.model;

public final class PetData {
    public final int id;
    public final int level;
    public final long exp;
    public final int hp;
    public final int mp;
    public final int pAtk;
    public final int pDef;
    public final int mAtk;
    public final int mDef;
    public final int accuracy;
    public final int evasion;
    public final int critical;
    public final int speed;
    public final int atkSpeed;
    public final int castSpeed;
    public final int feedMax;
    public final int feedBattle;
    public final int feedNormal;
    private final int maxLoad;
    private int foodId;
    private int minLevel;
    private int addFed;
    private boolean isMountable;

    public PetData(int id, int level, long exp, int hp, int mp, int patk, int pdef, int mAtk, int mDef,
                   int accuracy, int evasion, int critical, int speed, int atkSpeed, int castSpeed,
                   int feedMax, int feedBattle, int feedNormal, int maxLoad) {
        this.id = id;
        this.level = level;
        this.exp = exp;
        this.hp = hp;
        this.mp = mp;
        this.pAtk = patk;
        this.pDef = pdef;
        this.mAtk = mAtk;
        this.mDef = mDef;
        this.accuracy = accuracy;
        this.evasion = evasion;
        this.critical = critical;
        this.speed = speed;
        this.atkSpeed = atkSpeed;
        this.castSpeed = castSpeed;
        this.feedMax = feedMax;
        this.feedBattle = feedBattle;
        this.feedNormal = feedNormal;
        this.maxLoad = maxLoad;
    }

    public int getMaxLoad() {
        return maxLoad != 0 ? maxLoad : level * 300;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int id) {
        foodId = id;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int level) {
        minLevel = level;
    }

    public int getAddFed() {
        return addFed;
    }

    public void setAddFed(int addFed) {
        this.addFed = addFed;
    }

    public boolean isMountable() {
        return isMountable;
    }

    public void setMountable(boolean mountable) {
        isMountable = mountable;
    }
}