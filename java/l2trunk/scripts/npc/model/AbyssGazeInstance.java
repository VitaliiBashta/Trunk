package l2trunk.scripts.npc.model;

import l2trunk.gameserver.instancemanager.SoIManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.instances.HeartInfinityAttack;
import l2trunk.scripts.instances.HeartInfinityDefence;

public final class AbyssGazeInstance extends NpcInstance {
    private static final int ekimusIzId = 121;
    private static final int hoidefIzId = 122;

    public AbyssGazeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_permission".startsWith(command)) {
            if (SoIManager.getCurrentStage() == 2 || SoIManager.getCurrentStage() == 5) {
                showChatWindow(player, "default/32540-2.htm");
            } else if (SoIManager.getCurrentStage() == 3 && SoIManager.isSeedOpen()) {
                showChatWindow(player, "default/32540-3.htm");
            } else {
                showChatWindow(player, "default/32540-1.htm");
            }
        } else if ("request_ekimus".equalsIgnoreCase(command)) {
            if (SoIManager.getCurrentStage() == 2) {
                Reflection r = player.getActiveReflection();
                if (r != null) {
                    if (player.canReenterInstance(ekimusIzId))
                        player.teleToLocation(r.getTeleportLoc(), r);
                } else if (player.canEnterInstance(ekimusIzId)) {
                    ReflectionUtils.enterReflection(player, new HeartInfinityAttack(), ekimusIzId);
                }
            }
        } else if ("enter_seed".equalsIgnoreCase(command)) {
            if (SoIManager.getCurrentStage() == 3) {
                SoIManager.teleportInSeed(player);
            }
        } else if ("hoi_defence".equalsIgnoreCase(command)) {
            if (SoIManager.getCurrentStage() == 5) {
                Reflection r = player.getActiveReflection();
                if (r != null) {
                    if (player.canReenterInstance(hoidefIzId))
                        player.teleToLocation(r.getTeleportLoc(), r);
                } else if (player.canEnterInstance(hoidefIzId)) {
                    ReflectionUtils.enterReflection(player, new HeartInfinityDefence(), hoidefIzId);
                }
            }
        } else
            super.onBypassFeedback(player, command);
    }
}