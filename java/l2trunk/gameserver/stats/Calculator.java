package l2trunk.gameserver.stats;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncOwner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 * <p>
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR><BR>
 * <p>
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>_order</B>.
 * Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order.
 * The result of the calculation is stored in the value property of an Env class instance.<BR><BR>
 * <p>
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR><BR>
 */
public final class Calculator {
    public final Stats _stat;
    private final Creature _character;
    private List<Func> _functions;
    private double _base;
    private double _last;

    public Calculator(Stats stat, Creature character) {
        _stat = stat;
        _character = character;
        _functions = new ArrayList<>();
    }

    /**
     * Return the number of Funcs in the Calculator.<BR><BR>
     */
    public int size() {
        return _functions.size();
    }

    /**
     * Add a Func to the Calculator.<BR><BR>
     */
    public void addFunc(Func f) {
        _functions.add(f);
        Collections.sort(_functions);
    }

    /**
     * Remove a Func from the Calculator.<BR><BR>
     */
    public void removeFunc(Func f) {
        _functions.remove(f);
    }

    /**
     * Remove each Func with the specified owner of the Calculator.<BR><BR>
     */
    public void removeOwner(Object owner) {
        List<Func> tmp = _functions;
        _functions.removeIf(a->a.owner == owner);
//        for (Func element : tmp)
//            if (element.owner == owner)
//                removeFunc(element);
    }

    /**
     * Run each Func of the Calculator.<BR><BR>
     */
    @SuppressWarnings("unused")
    public void calc(Env env) {
        List<Func> funcs = _functions;
        _base = env.value;

        boolean overrideLimits = false;
        for (Func func : funcs) {
            if (func == null)
                continue;

            if (func.owner instanceof FuncOwner) {
                if (!((FuncOwner) func.owner).isFuncEnabled())
                    continue;
                if (((FuncOwner) func.owner).overrideLimits())
                    overrideLimits = true;
            }
            if (func.getCondition() == null || func.getCondition().test(env))
                func.calc(env);
        }

        if (!overrideLimits)
            env.value = _stat.validate(env.value);

        if (env.value != _last) {
            double last = _last; //TODO [G1ta0] найти приминение в StatsChangeRecorder
            _last = env.value;
        }
    }

    /**
     * for debugging
     */
    public List<Func> getFunctions() {
        return _functions;
    }

    public double getBase() {
        return _base;
    }

    public double getLast() {
        return _last;
    }
}