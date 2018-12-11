package l2trunk.scripts.ai.fog;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public final class GroupAI extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(GroupAI.class);

    private static final List<Integer> RANDOM_SPAWN_MOBS = Arrays.asList(
            18799, 18800, 18801, 18802, 18803);

    private static final List<Integer> FOG_MOBS = Arrays.asList(
            22634, 22635, 22636, 22637, 22638, 22639, 22640, 22641,
            22642, 22643, 22644, 22645, 22646, 22647, 22648, 22649);

    private GroupAI(NpcInstance actor) {
        super(actor);

        if (RANDOM_SPAWN_MOBS.contains(actor.getNpcId()))
            actor.startImmobilized();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance npc;
        NpcInstance actor = getActor();

        if (FOG_MOBS.contains(actor.getNpcId())) {
            try {
                npc = NpcHolder.getTemplate(Rnd.get(RANDOM_SPAWN_MOBS)).getNewInstance();
                npc.setSpawnedLoc(actor.getLoc());
                npc.setReflection(actor.getReflection());
                npc.setFullHpMp();
                npc.spawnMe(npc.getSpawnedLoc());
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
            } catch (RuntimeException e) {
                LOG.error("Error on GroupAI Death", e);
            }
        }

        super.onEvtDead(killer);
    }
}