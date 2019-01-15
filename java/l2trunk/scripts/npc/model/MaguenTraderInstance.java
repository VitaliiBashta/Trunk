package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

public final class MaguenTraderInstance extends NpcInstance {
    public MaguenTraderInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_collector".equalsIgnoreCase(command)) {
            if (Functions.getItemCount(player, 15487) > 0)
                showChatWindow(player, "default/32735-2.htm");
            else
                Functions.addItem(player, 15487, 1, "MaguenTraderInstance");
        } else if ("request_maguen".equalsIgnoreCase(command)) {
            NpcUtils.spawnSingle(18839, Location.findPointToStay(getSpawnedLoc(), 40, 100, getGeoIndex()), getReflection()); // wild maguen
            showChatWindow(player, "default/32735-3.htm");
        } else
            super.onBypassFeedback(player, command);
    }
}