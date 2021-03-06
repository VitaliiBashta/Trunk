package l2trunk.scripts.ai.seedofinfinity;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class WardofDeath extends DefaultAI {
    private static final List<Integer> mobs = List.of(22516, 22520, 22522, 22524);

    public WardofDeath(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        NpcInstance actor = getActor();
        if (target.isInRange(actor, actor.getAggroRange()) && !target.isDead() && !target.isInvisible()) {
            if (actor.getNpcId() == 18667) { // trap skill
                if (!avoidAttack) {
                    actor.doCast(Rnd.get(5423, 5424), 9, actor, false);
                    actor.doDie(null);
                }
                return true;
            } else if (actor.getNpcId() == 18668) // trap spawn
            {
                if (!avoidAttack) {
                    for (int i = 0; i < Rnd.get(1, 4); i++)
                        actor.getReflection().addSpawnWithoutRespawn(Rnd.get(mobs), actor.getLoc(), 100);
                    actor.doDie(null);
                }
                return true;
            }
        }
        return false;
    }
}