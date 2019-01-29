package l2trunk.gameserver.stats.funcs;

import l2trunk.gameserver.data.xml.holder.ClassesStatsBalancerHolder;
import l2trunk.gameserver.data.xml.parser.ClassesStatsBalancerParser;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;

import java.util.HashMap;
import java.util.Map;

public class FuncClassesBalancer extends Func {
    private static final Map<Stats, FuncClassesBalancer> _fh_instance = new HashMap<>();

    private FuncClassesBalancer(Stats stat, Object owner) {
        super(stat, 0x80, owner);
    }

    public static Func getInstance(Stats st, Creature cha) {
        if (!_fh_instance.containsKey(st))
            _fh_instance.put(st, new FuncClassesBalancer(st, cha));

        return _fh_instance.get(st);
    }

    @Override
    public void calc(Env env) {
        final ClassesStatsBalancerHolder balance = ClassesStatsBalancerParser.getInstance().getBalanceForClass(env.character.getPlayer().getClassId().id(), stat);
        if (balance != null) {
            // Apply the stats!
            env.value += balance.getFixedValue();
            env.value *= balance.getPercentValue();
        }
    }
}