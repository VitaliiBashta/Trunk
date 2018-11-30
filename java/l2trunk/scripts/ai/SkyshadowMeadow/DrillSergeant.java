package l2trunk.scripts.ai.SkyshadowMeadow;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;

import java.util.Arrays;
import java.util.List;

public final class DrillSergeant extends Fighter {
    private static final List<Integer> recruits = Arrays.asList(22780, 22782, 22783, 22784, 22785);
    private long waitTimeout = 0;

    public DrillSergeant(NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = 1000;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        if (System.currentTimeMillis() > waitTimeout) {
            waitTimeout = System.currentTimeMillis() + Rnd.get(10, 30) * 1000L;
            List<NpcInstance> around = actor.getAroundNpc(700, 100);
            int[] socialAction = {7, 4, 5};

            int random = Rnd.get(0, 2);
            if (around != null && !around.isEmpty()) {
                actor.broadcastPacket(new SocialAction(actor.getObjectId(), 7));
                around.stream()
                        .filter(mob -> recruits.contains(mob.getNpcId()))
                        .forEach(mob -> mob.broadcastPacket(new SocialAction(mob.getObjectId(), socialAction[random])));

            }
        }
        return false;
    }
}