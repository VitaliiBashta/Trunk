package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.instances.CrystalCaverns;

public final class SteamCorridorControllerInstance extends NpcInstance {

    public SteamCorridorControllerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("move_next".equalsIgnoreCase(command)) {
            if (getReflection().getInstancedZoneId() == 10)
                ((CrystalCaverns) getReflection()).notifyNextLevel(this);
        } else
            super.onBypassFeedback(player, command);
    }
}
