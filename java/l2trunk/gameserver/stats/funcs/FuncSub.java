package l2trunk.gameserver.stats.funcs;

import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

public class FuncSub extends Func {
    public FuncSub(Stats stat, int order, Object owner, double value) {
        super(stat, order, owner, value);
    }

    @Override
    public void calc(Env env) {
        env.value -= value;
    }
}
