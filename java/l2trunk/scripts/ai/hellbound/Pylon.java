package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Pylon extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(Pylon.class);

    private Pylon(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        for (int i = 0; i < 7; i++)
            try {
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getTemplate(22422));
                sp.setLoc(Location.findPointToStay(actor, 150, 550));
                sp.doSpawn(true);
                sp.stopRespawn();
            } catch (RuntimeException e) {
                LOG.error("Error on Pylon Spawn", e);
            }
    }
}