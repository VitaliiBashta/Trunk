package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class OriginalSinWarden6Floor extends Fighter {
    private static final List<Integer> DarionsFaithfulServants = List.of(22405, 22406, 22407);

    public OriginalSinWarden6Floor(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        if (Rnd.chance(15)) {
            new SimpleSpawner(Rnd.get(DarionsFaithfulServants))
                    .setLoc(Location.findPointToStay(actor, 150, 350))
                    .stopRespawn()
                    .doSpawn(true);
        }
        super.onEvtDead(killer);
    }

}