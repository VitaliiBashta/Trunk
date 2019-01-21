package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.templates.npc.MinionData;

import java.util.List;


public final class GraveRobberSummoner extends Mystic {
    private static final List<Integer> Servitors = List.of(22683, 22684, 22685, 22686);

    private int _lastMinionCount = 1;

    private class FuncMulMinionCount extends Func {
        FuncMulMinionCount(Stats stat, int order, Object owner) {
            super(stat, order, owner);
        }

        @Override
        public void calc(Env env) {
            env.value *= _lastMinionCount;
        }
    }

    public GraveRobberSummoner(NpcInstance actor) {
        super(actor);

        actor.addStatFunc(new FuncMulMinionCount(Stats.MAGIC_DEFENCE, 0x30, actor));
        actor.addStatFunc(new FuncMulMinionCount(Stats.POWER_DEFENCE, 0x30, actor));
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        actor.getMinionList().addMinion(new MinionData(Rnd.get(Servitors), Rnd.get(2)));
        _lastMinionCount = Math.max(actor.getMinionList().getAliveMinions().size(), 1);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        MonsterInstance actor = (MonsterInstance) getActor();
        if (actor.isDead())
            return;
        _lastMinionCount = Math.max(actor.getMinionList().getAliveMinions().size(), 1);
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        actor.getMinionList().deleteMinions();
        super.onEvtDead(killer);
    }
}