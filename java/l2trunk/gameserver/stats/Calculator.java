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
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>order</B>.
 * Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order.
 * The result of the calculation is stored in the value property of an Env class instance.<BR><BR>
 * <p>
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR><BR>
 */
public final class Calculator {
    public final Stats stat;
    private final Creature character;
    private List<Func> functions;
    private double base;
    private double last;

    public Calculator(Stats stat, Creature character) {
        this.stat = stat;
        this.character = character;
        functions = new ArrayList<>();
    }

    /**
     * Return the number of Funcs in the Calculator.<BR><BR>
     */
    public int size() {
        return functions.size();
    }

    /**
     * Add a Func to the Calculator.<BR><BR>
     */
    public void addFunc(Func f) {
        functions.add(f);
        Collections.sort(functions);
    }

    /**
     * Remove a Func from the Calculator.<BR><BR>
     */
    public void removeFunc(Func f) {
        functions.remove(f);
    }

    /**
     * Remove each Func with the specified owner of the Calculator.<BR><BR>
     */
    public void removeOwner(Object owner) {
        functions.removeIf(a -> a.owner == owner);
    }

    /**
     * Run each Func of the Calculator.<BR><BR>
     */
    public void calc(Env env) {
        List<Func> funcs = functions;
        base = env.value;

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
            env.value = stat.validate(env.value);

        if (env.value != last) {
            double last = this.last; //TODO [G1ta0] найти приминение в StatsChangeRecorder
            this.last = env.value;
        }
    }

    /**
     * for debugging
     */
    public List<Func> getFunctions() {
        return functions;
    }

    public double getBase() {
        return base;
    }

    public double getLast() {
        return last;
    }
}