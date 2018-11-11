package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.templates.StatsSet;

/**
 * @author DevGrivesky
 */
public class NpcStatsBalancerHolder {
    private final Stats _stat;
    private final int _fValue;
    private final float _pValue;

    public NpcStatsBalancerHolder(StatsSet set) {
        _stat = Stats.valueOfXml(set.getString("name"));
        _fValue = set.getInteger("fValue", 0);
        _pValue = set.getFloat("pValue", 1.0f);
    }

    public Stats getStat() {
        return _stat;
    }

    public int getFixedValue() {
        return _fValue;
    }

    public float getPercentValue() {
        return _pValue;
    }
}