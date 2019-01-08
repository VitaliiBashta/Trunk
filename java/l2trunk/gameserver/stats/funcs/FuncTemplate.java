package l2trunk.gameserver.stats.funcs;

import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.Condition;

public final class FuncTemplate {
    public final Stats stat;
    public final int order;
    public final double value;
    private final Condition applyCond;
    private String func;

    public FuncTemplate(Condition applyCond, String func, Stats stat, int order, double value) {
        this.applyCond = applyCond;
        this.stat = stat;
        this.order = order;
        this.value = value;
        this.func = func;
    }

    public Func getFunc(Object owner) {
        Func f = getFunc0(owner);
        if (applyCond != null)
            f.setCondition(applyCond);
        return f;
    }

    private Func getFunc0(Object owner) {
        switch (func) {
            case "Add":
                return new FuncAdd(stat, order, owner, value);
            case "Div":
                return new FuncDiv(stat, order, owner, value);
            case "Mul":
                return new FuncMul(stat, order, owner, value);
            case "Set":
                return new FuncSet(stat, order, owner, value);
            case "Sub":
                return new FuncSub(stat, order, owner, value);
            case "Enchant":
                return new FuncEnchant(stat, order, owner, value);

            default:
                throw new IllegalArgumentException("no func for name " + func);
        }
    }
}