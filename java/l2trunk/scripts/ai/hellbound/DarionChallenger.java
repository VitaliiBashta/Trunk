package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DarionChallenger extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(DarionChallenger.class);
    private static final int TeleportCube = 32467;

    private DarionChallenger(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (checkAllDestroyed())
            try {
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getTemplate(TeleportCube));
                sp.setLoc(new Location(-12527, 279714, -11622, 16384));
                sp.doSpawn(true);
                sp.stopRespawn();
                ThreadPoolManager.INSTANCE.schedule(new Unspawn(), 600 * 1000L); // 10 mins
            } catch (RuntimeException e) {
                LOG.error("Error on Darino Challanger Spawn", e);
            }
        super.onEvtDead(killer);
    }

    private boolean checkAllDestroyed() {
        if (!GameObjectsStorage.getAllByNpcId(25600, true).isEmpty())
            return false;
        if (!GameObjectsStorage.getAllByNpcId(25601, true).isEmpty())
            return false;
        return GameObjectsStorage.getAllByNpcId(25602, true).isEmpty();
    }

    private class Unspawn extends RunnableImpl {
        Unspawn() {
        }

        @Override
        public void runImpl() {
            for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(TeleportCube, true))
                npc.deleteMe();
        }
    }
}