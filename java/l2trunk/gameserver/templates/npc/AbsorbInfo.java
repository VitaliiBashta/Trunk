package l2trunk.gameserver.templates.npc;

import java.util.HashSet;
import java.util.Set;

public final class AbsorbInfo {
    private final boolean skill;
    private final AbsorbType absorbType;
    private final int chance;
    private final int cursedChance;
    private final Set<Integer> levels;

    public AbsorbInfo(boolean skill, AbsorbType absorbType, int chance, int cursedChance, int min, int max) {
        this.skill = skill;
        this.absorbType = absorbType;
        this.chance = chance;
        this.cursedChance = cursedChance;
        levels = new HashSet<>(max - min);
        for (int i = min; i <= max; i++)
            levels.add(i);
    }

    public boolean isSkill() {
        return skill;
    }

    public AbsorbType getAbsorbType() {
        return absorbType;
    }

    public int getChance() {
        return chance;
    }

    public int getCursedChance() {
        return cursedChance;
    }

    public boolean canAbsorb(int le) {
        return levels.contains(le);
    }

    public enum AbsorbType {
        LAST_HIT,
        PARTY_ONE,
        PARTY_ALL,
        PARTY_RANDOM
    }
}
