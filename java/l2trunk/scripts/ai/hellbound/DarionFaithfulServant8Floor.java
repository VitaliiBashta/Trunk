package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

public final class DarionFaithfulServant8Floor extends Fighter {
    private static final int MysteriousAgent = 32372;

    public DarionFaithfulServant8Floor(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        if (Rnd.chance(15) && actor.isInZone("[tully8_room1]"))
            NpcUtils.spawnSingle(MysteriousAgent, new Location(-13312, 279172, -10492, -20300), 600000L);
        if (Rnd.chance(15) && actor.isInZone("[tully8_room2]"))
            NpcUtils.spawnSingle(MysteriousAgent, new Location(-11696, 280208, -10492, 13244), 600000L);
        if (Rnd.chance(15) && actor.isInZone("[tully8_room3]"))
            NpcUtils.spawnSingle(MysteriousAgent, new Location(-13008, 280496, -10492, 27480), 600000L);
        if (Rnd.chance(15) && actor.isInZone("[tully8_room4]"))
            NpcUtils.spawnSingle(MysteriousAgent, new Location(-11984, 278880, -10492, -4472), 600000L);
        super.onEvtDead(killer);
    }
}