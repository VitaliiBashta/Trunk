package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OriginalSinWarden extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(OriginalSinWarden.class);
    private static final int[] servants1 = {22424, 22425, 22426, 22427, 22428, 22429, 22430};
    private static final int[] servants2 = {22432, 22433, 22434, 22435, 22436, 22437, 22438};
    private static final int[] DarionsFaithfulServants = {22405, 22406, 22407};

    private OriginalSinWarden(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        switch (actor.getNpcId()) {
            case 22423: {
                for (int aServants1 : servants1)
                    try {
                        //Location loc = actor.getLoc();
                        SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(aServants1));
                        sp.setLoc(Location.findPointToStay(actor, 150, 350));
                        sp.doSpawn(true);
                        sp.stopRespawn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                break;
            }
            case 22431: {
                for (int aServants2 : servants2)
                    try {
                        //Location loc = actor.getLoc();
                        SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(aServants2));
                        sp.setLoc(Location.findPointToStay(actor, 150, 350));
                        sp.doSpawn(true);
                        sp.stopRespawn();
                    } catch (RuntimeException e) {
                        LOG.error("Error on Original Sin Warden Servants Spawn", e);
                    }
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        if (Rnd.chance(15))
            try {
                //Location loc = actor.getLoc();
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(DarionsFaithfulServants[Rnd.get(DarionsFaithfulServants.length - 1)]));
                sp.setLoc(Location.findPointToStay(actor, 150, 350));
                sp.doSpawn(true);
                sp.stopRespawn();
            } catch (RuntimeException e) {
                LOG.error("Error on Original Sin Warden Death", e);
            }
        super.onEvtDead(killer);
    }

}