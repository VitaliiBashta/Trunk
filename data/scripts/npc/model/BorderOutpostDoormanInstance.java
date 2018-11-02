package npc.model;

import l2f.gameserver.model.Player;
import l2f.gameserver.model.instances.DoorInstance;
import l2f.gameserver.model.instances.GuardInstance;
import l2f.gameserver.templates.npc.NpcTemplate;
import l2f.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 10:26/24.06.2011
 */
public class BorderOutpostDoormanInstance extends GuardInstance {
    public BorderOutpostDoormanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equals("openDoor")) {
            DoorInstance door = ReflectionUtils.getDoor(24170001);
            door.openMe();
        } else if (command.equals("closeDoor")) {
            DoorInstance door = ReflectionUtils.getDoor(24170001);
            door.closeMe();
        } else
            super.onBypassFeedback(player, command);
    }
}
