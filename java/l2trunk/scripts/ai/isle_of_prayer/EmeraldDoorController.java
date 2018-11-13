package l2trunk.scripts.ai.isle_of_prayer;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.scripts.instances.CrystalCaverns;

public final class EmeraldDoorController extends DefaultAI {
    private boolean openedDoor = false;
    private Player opener = null;

    public EmeraldDoorController(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        DoorInstance door = getClosestDoor();
        boolean active = false;
        CrystalCaverns refl = null;
        if (actor.getReflection() instanceof CrystalCaverns)
            refl = (CrystalCaverns) actor.getReflection();
        if (refl != null)
            active = refl.areDoorsActivated();
        if (door != null && active) {
            for (Creature c : getActor().getAroundCharacters(250, 150))
                if (!openedDoor && c.isPlayer() && ItemFunctions.getItemCount(c.getPlayer(), 9694) > 0) // Secret Key
                {
                    openedDoor = true;
                    ItemFunctions.removeItem(c.getPlayer(), 9694, 1, true, "EmeraldDoorController");
                    door.openMe();
                    opener = c.getPlayer();
                }

            boolean found = false;
            if (opener != null)
                for (Creature c : getActor().getAroundCharacters(250, 150))
                    if (openedDoor && c.isPlayer() && c.getPlayer() == opener)
                        found = true;

            if (!found)
                door.closeMe();
        }
        return super.thinkActive();
    }

    private DoorInstance getClosestDoor() {
        NpcInstance actor = getActor();
        for (Creature c : actor.getAroundCharacters(200, 200))
            if (c.isDoor())
                return (DoorInstance) c;
        return null;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}