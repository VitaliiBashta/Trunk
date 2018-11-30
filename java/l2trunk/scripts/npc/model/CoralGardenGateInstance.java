package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.instances.CrystalCaverns;

public class CoralGardenGateInstance extends NpcInstance {
    public CoralGardenGateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("request_coralg")) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(10))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(10)) {
                ReflectionUtils.enterReflection(player, new CrystalCaverns(), 10);
            }
        } else
            super.onBypassFeedback(player, command);
    }
}
