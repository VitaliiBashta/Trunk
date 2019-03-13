package l2trunk.gameserver.stats;

import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncOwner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).
 * In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <BR><BR>
 * <p>
 * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.occupation()<BR><BR>
 * <p>
 * When the calc method of a calculator is launched, each mathematic function is called according to its priority <B>order</B>.
 * Indeed, Func with lowest priority order is executed firsta and Funcs with the same order are executed in unspecified order.
 * The result of the calculation is stored in the value property of an Env class instance.<BR><BR>
 * <p>
 * Method addFunc and removeFunc permit to add and remove a Func object from a Calculator.<BR><BR>
 */
public final class Calculator {
    public final Stats stat;
    private final List<Func> functions = new ArrayList<>();
    private double base;
    private double last;

    public Calculator(Stats stat) {
        this.stat = stat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calculator that = (Calculator) o;
        return stat == that.stat ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stat);
    }

    /**
     * Add a Func to the Calculator.<BR><BR>
     */
    public void addFunc(Func f) {
        functions.add(f);
        functions.sort((Comparator.comparingInt(o -> o.order)));
    }


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
        base = env.value;

        for (Func func : functions) {
            if (func != null) {
                if (!(func.owner instanceof FuncOwner) || ((FuncOwner) func.owner).isFuncEnabled()) {

                    if (func.getCondition() == null || func.getCondition().test(env))
                        func.calc(env);
                }
            }

        }

        env.value = stat.validate(env.value);

        if (env.value != last) {
            this.last = env.value;
        }
    }


    public List<Func> getFunctions() {
        return functions;
    }

    public double getBase() {
        return base;
    }

    public double getLast() {
        return last;
    }

    @Override
    public String toString() {
        return stat +
               " " + functions.toString()  +
                " base=" + base +
                ", last=" + last;
    }
}