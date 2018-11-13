package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class AliveTumor extends DefaultAI {
    private long checkTimer = 0;
    private int coffinsCount = 0;
    private static final int[] regenCoffins = {18706, 18709, 18710};

    public AliveTumor(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        if (checkTimer + 10000 < System.currentTimeMillis()) {
            checkTimer = System.currentTimeMillis();
            int i = 0;
            for (NpcInstance n : actor.getAroundNpc(400, 300))
                if (ArrayUtils.contains(regenCoffins, n.getNpcId()) && !n.isDead())
                    i++;
            if (coffinsCount != i) {
                coffinsCount = i;
                coffinsCount = Math.min(coffinsCount, 12);
                if (coffinsCount > 0)
                    actor.altOnMagicUseTimer(actor, SkillTable.getInstance().getInfo(5940, coffinsCount));
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