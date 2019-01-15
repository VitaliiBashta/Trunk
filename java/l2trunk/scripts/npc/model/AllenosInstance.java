package l2trunk.scripts.npc.model;

import l2trunk.gameserver.instancemanager.SoDManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class AllenosInstance extends NpcInstance {
    private static final int tiatIzId = 110;

    public AllenosInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("enter_seed".equalsIgnoreCase(command)) {
            // Время открытого SoD прошло
            if (SoDManager.isAttackStage()) {
                Reflection r = player.getActiveReflection();
                if (r != null) {
                    if (player.canReenterInstance(tiatIzId))
                        player.teleToLocation(r.getTeleportLoc(), r);
                } else if (player.canEnterInstance(tiatIzId)) {
                    ReflectionUtils.enterReflection(player, tiatIzId);
                }
            } else
                SoDManager.teleportIntoSeed(player);
        } else
            super.onBypassFeedback(player, command);
    }
}