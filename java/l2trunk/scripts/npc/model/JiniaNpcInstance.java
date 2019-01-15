package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.instances.FreyaHard;
import l2trunk.scripts.instances.FreyaNormal;

public final class JiniaNpcInstance extends NpcInstance {
    private static final int normalFreyaIzId = 139;
    private static final int extremeFreyaIzId = 144;

    public JiniaNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_normalfreya".equalsIgnoreCase(command)) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(normalFreyaIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(normalFreyaIzId)) {
                ReflectionUtils.enterReflection(player, new FreyaNormal(), normalFreyaIzId);
            }
        } else if ("request_extremefreya".equalsIgnoreCase(command)) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(extremeFreyaIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(extremeFreyaIzId)) {
                ReflectionUtils.enterReflection(player, new FreyaHard(), extremeFreyaIzId);
            }
        } else
            super.onBypassFeedback(player, command);
    }
}