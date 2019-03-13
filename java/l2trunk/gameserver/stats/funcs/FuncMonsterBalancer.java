package l2trunk.gameserver.stats.funcs;

import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

import java.util.HashMap;
import java.util.Map;

public final class FuncMonsterBalancer extends Func {
    private static final Map<Stats, FuncMonsterBalancer> MONSTER_BALANCER = new HashMap<>();

    private FuncMonsterBalancer(Stats stat) {
        super(stat, 0x80, null);
    }

    public static Func getInstance(Stats st) {
        if (!MONSTER_BALANCER.containsKey(st))
            MONSTER_BALANCER.put(st, new FuncMonsterBalancer(st));

        return MONSTER_BALANCER.get(st);
    }

    @Override
    public void calc(Env env) {
    }
}