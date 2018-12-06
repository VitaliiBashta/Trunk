package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class DarionChallenger extends Fighter {
    private static final int TeleportCube = 32467;

    private DarionChallenger(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (checkAllDestroyed()) {
            new SimpleSpawner(TeleportCube)
                    .setLoc(new Location(-12527, 279714, -11622, 16384))
                    .stopRespawn()
                    .doSpawn(true);
            ThreadPoolManager.INSTANCE.schedule(() -> GameObjectsStorage.getAllByNpcId(TeleportCube, true).forEach(GameObject::deleteMe),
                    600 * 1000L); // 10 mins
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

}