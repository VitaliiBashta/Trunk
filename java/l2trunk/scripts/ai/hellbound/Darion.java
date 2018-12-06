package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.Arrays;
import java.util.List;

public final class Darion extends Fighter {
    private static final List<Integer> doors = Arrays.asList(
            20250009, 20250004, 20250005, 20250006, 20250007);

    private Darion(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        for (int i = 0; i < 5; i++) {
            new SimpleSpawner(Rnd.get(25614, 25615))
                    .setLoc(Location.findPointToStay(actor, 400, 900))
                    .stopRespawn()
                    .doSpawn(true);
        }

        doors.forEach(door -> ReflectionUtils.getDoor(door).closeMe());
    }

    @Override
    public void onEvtDead(Creature killer) {
        //Doors
        doors.forEach(door -> ReflectionUtils.getDoor(door).openMe());
        GameObjectsStorage.getAllByNpcId(25614, false).forEach(GameObject::deleteMe);
        GameObjectsStorage.getAllByNpcId(25615, false).forEach(GameObject::deleteMe);
        super.onEvtDead(killer);
    }

}