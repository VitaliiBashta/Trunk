package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class AliveTumor extends DefaultAI {
    private static final List<Integer> regenCoffins = List.of(18706, 18709, 18710);
    private long checkTimer = 0;
    private int coffinsCount = 0;

    public AliveTumor(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        if (checkTimer + 10000 < System.currentTimeMillis()) {
            checkTimer = System.currentTimeMillis();
            int i = (int) actor.getAroundNpc(400, 300)
                    .filter(n -> regenCoffins.contains(n.getNpcId()))
                    .filter(n -> !n.isDead())
                    .count();
            if (coffinsCount != i) {
                coffinsCount = i;
                coffinsCount = Math.min(coffinsCount, 12);
                if (coffinsCount > 0)
                    actor.altOnMagicUseTimer(actor, 5940, coffinsCount);
            }
        }
        return super.thinkActive();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}