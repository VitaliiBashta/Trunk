package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class Pylon extends Fighter {
    public Pylon(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        for (int i = 0; i < 7; i++) {
            new SimpleSpawner(22422)
                    .setLoc(Location.findPointToStay(actor, 150, 550))
                    .stopRespawn()
                    .doSpawn(true);
        }
    }
}