package l2trunk.gameserver.templates.npc;

public final class AbsorbInfo {
    public final boolean isSkill;
    public final AbsorbType absorbType;
    public final int chance;
    public final int cursedChance;
    private final int minLevel;
    private final int maxLevel;

    public AbsorbInfo(boolean skill, AbsorbType absorbType, int chance, int cursedChance, int min, int max) {
        this.isSkill = skill;
        this.absorbType = absorbType;
        this.chance = chance;
        this.cursedChance = cursedChance;
        this.minLevel = min;
        this.maxLevel = max;
    }

    public boolean canAbsorb(int lvl) {
        return lvl >= minLevel && lvl <= maxLevel;
    }

    public enum AbsorbType {
        LAST_HIT,
        PARTY_ONE,
        PARTY_ALL,
        PARTY_RANDOM
    }
}
