package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.gameserver.Config;

public enum CompType {
    TEAM(2, Config.ALT_OLY_TEAM_RITEM_C, 5, false),
    NON_CLASSED(2, Config.ALT_OLY_NONCLASSED_RITEM_C, 5, true),
    CLASSED(2, Config.ALT_OLY_CLASSED_RITEM_C, 3, true);

    private final int _minSize;
    private final int _reward;
    private final int _looseMult;
    private final boolean _hasBuffer;

    CompType(int minSize, int reward, int looseMult, boolean hasBuffer) {
        _minSize = minSize;
        _reward = reward;
        _looseMult = looseMult;
        _hasBuffer = hasBuffer;
    }

    public int getMinSize() {
        return _minSize;
    }

    public int getReward() {
        return _reward;
    }

    public int getLooseMult() {
        return _looseMult;
    }

    public boolean hasBuffer() {
        return _hasBuffer;
    }
}