package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.templates.StatsSet;

public final class ClassesStatsBalancerHolder {
    private final Stats stat;
    private final int fValue;
    private final float pValue;

    public ClassesStatsBalancerHolder(StatsSet set) {
        stat = Stats.valueOfXml(set.getString("name"));
        fValue = set.getInteger("fValue", 0);
        pValue = set.getFloat("pValue", 1.0f);
    }

    public Stats getStat() {
        return stat;
    }

    public int getFixedValue() {
        return fValue;
    }

    public float getPercentValue() {
        return pValue;
    }
}