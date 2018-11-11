package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ReflectionUtils;

public class MasterZelos extends Fighter {
    private static Zone _zone;
    private static final int[] doors = {19260054, 19260053};

    public MasterZelos(NpcInstance actor) {
        super(actor);
        _zone = ReflectionUtils.getZone("[tully1]");
    }

    @Override
    protected void onEvtSpawn() {
        setZoneInactive();
        super.onEvtSpawn();
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
        super.onEvtDead(killer);
        setZoneActive();
    }

    private void setZoneActive() {
        _zone.setActive(true);
    }

    private void setZoneInactive() {
        _zone.setActive(false);
    }
}