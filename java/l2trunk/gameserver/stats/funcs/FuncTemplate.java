package l2trunk.gameserver.stats.funcs;

import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class FuncTemplate {
    public static final FuncTemplate[] EMPTY_ARRAY = new FuncTemplate[0];
    private static final Logger _log = LoggerFactory.getLogger(FuncTemplate.class);
    public final Stats _stat;
    public final int _order;
    public final double _value;
    private final Condition _applyCond;
    private Class<?> _func;
    private Constructor<?> _constructor;

    public FuncTemplate(Condition applyCond, String func, Stats stat, int order, double value) {
        _applyCond = applyCond;
        _stat = stat;
        _order = order;
        _value = value;

        try {
            _func = Class.forName("l2trunk.gameserver.stats.funcs.Func" + func);

            _constructor = _func.getConstructor(Stats.class, // stats to update
                    Integer.TYPE, // order of execution
                    Object.class, // owner
                    Double.TYPE // value for function
            );
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException e) {
            _log.error("Error while creating FuncTemplate ", e);
        }
    }

    public Func getFunc(Object owner) {
        try {
            Func f = (Func) _constructor.newInstance(_stat, _order, owner, _value);
            if (_applyCond != null)
                f.setCondition(_applyCond);
            return f;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            _log.error("Error while getting Function!", e);
            return null;
        }
    }
}