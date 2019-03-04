package l2trunk.gameserver.templates;

public final class FishTemplate {
    public final int id;
    public final int level;
    public final int hp;
    public final int hpRegen;
    public final int type;
    public final int group;
    public final int fishGuts;
    public final int gutsCheckTime;
    public final int waitTime;
    public final int combatTime;

    public FishTemplate(int id, int level, int hp, int hpRegen, int type, int group, int fishGuts, int gutsCheckTime, int waitTime, int combatTime) {
        this.id = id;
        this.level = level;
        this.hp = hp;
        this.hpRegen = hpRegen;
        this.type = type;
        this.group = group;
        this.fishGuts = fishGuts;
        this.gutsCheckTime = gutsCheckTime;
        this.waitTime = waitTime;
        this.combatTime = combatTime;
    }


}
