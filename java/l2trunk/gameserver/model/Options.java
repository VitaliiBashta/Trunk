package l2trunk.gameserver.model;

import l2trunk.gameserver.stats.triggers.TriggerInfo;
import l2trunk.gameserver.stats.triggers.TriggerType;

public final class Options {
    private final int id;
    private final String augType;
    private final TriggerInfo trigger;

    public Options(int augId, String augType, int skillId, int skillLevel, TriggerType triggerType, double triggerChance) {
        id = augId;
        this.augType = augType;
        trigger = new TriggerInfo(skillId, skillLevel, triggerType, triggerChance);
    }

    public int getAugmentationId() {
        return id;
    }

    public TriggerInfo getTrigger() {
        return trigger;
    }

    public enum AugmentationFilter {
        NONE,
        ACTIVE_SKILL,
        PASSIVE_SKILL,
        CHANCE_SKILL,
        STATS
    }
}
