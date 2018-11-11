package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.instances.ZakenDay;
import l2trunk.scripts.instances.ZakenDay83;
import l2trunk.scripts.instances.ZakenNight;

/**
 * @author pchayka
 */

public final class ZakenGatekeeperInstance extends NpcInstance {
    private static final int nightZakenIzId = 114;
    private static final int dayZakenIzId = 133;
    private static final int ultraZakenIzId = 135;

    public ZakenGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("request_nightzaken")) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(nightZakenIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(nightZakenIzId)) {
                ReflectionUtils.enterReflection(player, new ZakenNight(), nightZakenIzId);
            }
        } else if (command.equalsIgnoreCase("request_dayzaken")) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(dayZakenIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(dayZakenIzId)) {
                ReflectionUtils.enterReflection(player, new ZakenDay(), dayZakenIzId);
            }
        } else if (command.equalsIgnoreCase("request_ultrazaken")) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(ultraZakenIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(ultraZakenIzId)) {
                ReflectionUtils.enterReflection(player, new ZakenDay83(), ultraZakenIzId);
            }
        } else
            super.onBypassFeedback(player, command);
    }
}