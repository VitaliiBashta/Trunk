package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;

/**
 * @author pchayka
 */

public final class GruffManInstance extends NpcInstance {
    private static final int elcardiaIzId = 158;

    public GruffManInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("elcardia_enter")) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(elcardiaIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(elcardiaIzId)) {
                ReflectionUtils.enterReflection(player, elcardiaIzId);
            }
        } else
            super.onBypassFeedback(player, command);
    }
}