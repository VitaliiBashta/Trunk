package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Tully extends Fighter {
    // 32371
    private static final List<Location> locSD = List.of(
            new Location(-12524, 273932, -9014, 49151),
            new Location(-10831, 273890, -9040, 81895),
            new Location(-10817, 273986, -9040, -16452),
            new Location(-13773, 275119, -9040, 8428),
            new Location(-11547, 271772, -9040, -19124));

    //22392
    private static final List<Location> locFTT = List.of(
            new Location(-10832, 273808, -9040, 0),
            new Location(-10816, 274096, -9040, 14964),
            new Location(-13824, 275072, -9040, -24644),
            new Location(-11504, 271952, -9040, 9328));
    private static NpcInstance removableGhost = null;
    private boolean s = false;

    public Tully(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        locSD.forEach(l -> {
            SimpleSpawner sp = new SimpleSpawner(32371).setLoc(l);
            sp.doSpawn(true);
            if (!s) {
                Functions.npcShout(sp.getLastSpawn(), "Self Destruction mechanism launched: 10 minutes to " +
                        "explosion");
                s = true;
            }

        });
        locFTT.forEach(l -> new SimpleSpawner(22392).setLoc(l).doSpawn(true));
        SimpleSpawner sp = new SimpleSpawner(32370)
                .setLoc(new Location(-11984, 272928, -9040, 23644));
        sp.doSpawn(true);
        removableGhost = sp.getLastSpawn();
        ThreadPoolManager.INSTANCE.schedule(() -> {
            GameObjectsStorage.getAllByNpcId(32371, true).forEach(GameObject::deleteMe);
            GameObjectsStorage.getAllByNpcId(22392, true).forEach(GameObject::deleteMe);
            if (removableGhost != null) removableGhost.deleteMe();
        }, 600 * 1000L); // 10 mins

        super.onEvtDead(killer);
    }
}