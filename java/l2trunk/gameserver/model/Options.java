package l2trunk.gameserver.model;

import l2trunk.gameserver.stats.triggers.TriggerInfo;
import l2trunk.gameserver.stats.triggers.TriggerType;

public class Options {
    private final int _id;
    private final String _augType;
    private final TriggerInfo _trigger;

    public Options(int augId, String augType, int skillId, int skillLevel, TriggerType triggerType, double triggerChance) {
        _id = augId;
        _augType = augType;
        _trigger = new TriggerInfo(skillId, skillLevel, triggerType, triggerChance);
    }

    public int getAugmentationId() {
        return _id;
    }

    public String getAugType() {
        return _augType;
    }

    public TriggerInfo getTrigger() {
        return _trigger;
    }

    public enum AugmentationFilter {
        NONE,
        ACTIVE_SKILL,
        PASSIVE_SKILL,
        CHANCE_SKILL,
        STATS
    }
}
