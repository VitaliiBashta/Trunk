package l2trunk.scripts.ai.selmahum;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;

import java.util.List;
import java.util.stream.Stream;

public final class DrillSergeant extends Fighter {
    private static final List<Integer> recruits = List.of(22780, 22782, 22783, 22784, 22785);
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
            Stream<NpcInstance> around = actor.getAroundNpc(700, 100).filter(mob -> recruits.contains(mob.getNpcId()));
            actor.broadcastPacket(new SocialAction(actor.objectId(), 7));
            switch (Rnd.get(1, 3)) {
                case 1:
                    around.forEach(mob ->
                            mob.broadcastPacket(new SocialAction(mob.objectId(), 7)));
                    break;
                case 2:
                    around.forEach(mob ->
                            mob.broadcastPacket(new SocialAction(mob.objectId(), 4)));
                    break;
                case 3:
                    around.forEach(mob ->
                            mob.broadcastPacket(new SocialAction(mob.objectId(), 5)));
                    break;
            }
        }
        return false;
    }
}