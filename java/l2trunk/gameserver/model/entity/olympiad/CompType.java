package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.gameserver.Config;

public enum CompType {
    TEAM(2, Config.ALT_OLY_TEAM_RITEM_C, 5, false),
    NON_CLASSED(2, Config.ALT_OLY_NONCLASSED_RITEM_C, 5, true),
    CLASSED(2, Config.ALT_OLY_CLASSED_RITEM_C, 3, true);

    private final int minSize;
    private final int reward;
    private final int looseMult;
    private final boolean hasBuffer;

    CompType(int minSize, int reward, int looseMult, boolean hasBuffer) {
        this.minSize = minSize;
        this.reward = reward;
        this.looseMult = looseMult;
        this.hasBuffer = hasBuffer;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getReward() {
        return reward;
    }

    public int getLooseMult() {
        return looseMult;
    }

    public boolean hasBuffer() {
        return hasBuffer;
    }
}