package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class BatracosInstance extends NpcInstance {
    private static final int urogosIzId = 505;

    public BatracosInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (val == 0) {
            String htmlpath;
            if (getReflection().isDefault())
                htmlpath = "default/32740.htm";
            else
                htmlpath = "default/32740-4.htm";
            player.sendPacket(new NpcHtmlMessage(player, this, htmlpath, val));
        } else
            super.showChatWindow(player, val);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_seer".equalsIgnoreCase(command)) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(urogosIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(urogosIzId)) {
                ReflectionUtils.enterReflection(player, urogosIzId);
            }
        } else if ("leave".equalsIgnoreCase(command)) {
            if (!getReflection().isDefault())
                getReflection().collapse();
        } else
            super.onBypassFeedback(player, command);
    }
}