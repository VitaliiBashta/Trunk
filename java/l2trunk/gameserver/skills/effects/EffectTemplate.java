package l2trunk.gameserver.skills.effects;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.EffectList;
import l2trunk.gameserver.skills.AbnormalEffect;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.StatTemplate;
import l2trunk.gameserver.stats.conditions.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

public final class EffectTemplate extends StatTemplate {
    public static final String NO_STACK = "none";
    public static final String HP_RECOVER_CAST = "HpRecoverCast";
    private static final Logger _log = LoggerFactory.getLogger(EffectTemplate.class);
    public final double value;
    public final int count;
    public final EffectType effecttype;
    public final String stackType;
    public final String stackType2;
    public final int stackOrder;
    public final int displayId;
    public final int displayLevel;
    public final boolean applyOnCaster;
    public final boolean _applyOnSummon;
    public final boolean _cancelOnAction;
    public final boolean _isReflectable;
    public final AbnormalEffect _abnormalEffect;
    public final AbnormalEffect _abnormalEffect2;
    public final AbnormalEffect _abnormalEffect3;
    private final long _period; // in milliseconds
    private final Boolean isSaveable;
    private final Boolean isCancelable;
    private final Boolean isOffensive;
    private final StatsSet param;
    private final int chance;
    private Condition attachCond;

    public EffectTemplate(StatsSet set) {
        value = set.getDouble("value");
        count = set.getInteger("count", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("count", 1);
        _period = Math.min(Integer.MAX_VALUE, 1000 * (set.getInteger("time", 1) < 0 ? Integer.MAX_VALUE : set.getInteger("time", 1)));
        _abnormalEffect = set.getEnum("abnormal", AbnormalEffect.class);
        _abnormalEffect2 = set.getEnum("abnormal2", AbnormalEffect.class);
        _abnormalEffect3 = set.getEnum("abnormal3", AbnormalEffect.class);
        stackType = set.getString("stackType", NO_STACK);
        stackType2 = set.getString("stackType2", NO_STACK);
        stackOrder = set.getInteger("stackOrder", stackType.equals(NO_STACK) && stackType2.equals(NO_STACK) ? 1 : 0);
        applyOnCaster = set.getBool("applyOnCaster", false);
        _applyOnSummon = set.getBool("applyOnSummon", true);
        _cancelOnAction = set.getBool("cancelOnAction", false);
        _isReflectable = set.getBool("isReflectable", true);
        isSaveable = set.isSet("isSaveable") ? set.getBool("isSaveable") : null;
        isCancelable = set.isSet("isCancelable") ? set.getBool("isCancelable") : null;
        isOffensive = set.isSet("isOffensive") ? set.getBool("isOffensive") : null;
        displayId = set.getInteger("displayId", 0);
        displayLevel = set.getInteger("displayLevel", 0);
        effecttype = set.getEnum("name", EffectType.class);
        chance = set.getInteger("chance", Integer.MAX_VALUE);
        param = set;
    }

    public Effect getEffect(Env env) {
        if (attachCond != null && !attachCond.test(env))
            return null;
        try {
            return effecttype.makeEffect(env, this);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException e) {
            _log.error("Error while getting Effect ", e);
        }
        return null;
    }

    public void attachCond(Condition c) {
        attachCond = c;
    }

    public int getCount() {
        return count;
    }

    public long getPeriod() {
        return _period;
    }

    public EffectType getEffectType() {
        return effecttype;
    }

    public Effect getSameByStackType(List<Effect> list) {
        return list.stream().filter(Objects::nonNull)
                .filter(ef -> EffectList.checkStackType(ef.getTemplate(), this))
                .findFirst().orElse(null);
    }

    public StatsSet getParam() {
        return param;
    }

    public int chance(int val) {
        return chance == Integer.MAX_VALUE ? val : chance;
    }

    public boolean isSaveable(boolean def) {
        return isSaveable != null ? isSaveable : def;
    }

    public boolean isCancelable(boolean def) {
        return isCancelable != null ? isCancelable : def;
    }

    public boolean isOffensive(boolean def) {
        return isOffensive != null ? isOffensive : def;
    }
}