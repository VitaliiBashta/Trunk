package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Darion extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(Darion.class);

    private static final int[] doors = {
            20250009,
            20250004,
            20250005,
            20250006,
            20250007
    };

    private Darion(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        for (int i = 0; i < 5; i++)
            try {
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(Rnd.get(25614, 25615)));
                sp.setLoc(Location.findPointToStay(actor, 400, 900));
                sp.doSpawn(true);
                sp.stopRespawn();
            } catch (RuntimeException e) {
                LOG.error("Error on Darion Spawn", e);
            }

        //Doors
        for (int door1 : doors) {
            DoorInstance door = ReflectionUtils.getDoor(door1);
            door.closeMe();
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {
        //Doors
        for (int door1 : doors) {
            DoorInstance door = ReflectionUtils.getDoor(door1);
            door.openMe();
        }

        for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(25614, false))
            npc.deleteMe();

        for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(25615, false))
            npc.deleteMe();

        super.onEvtDead(killer);
    }

}