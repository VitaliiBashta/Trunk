package l2trunk.scripts.npc.model;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

public final class KeplonInstance extends NpcInstance {
    public KeplonInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (checkForDominionWard(player))
            return;

        if ("buygreen".equalsIgnoreCase(command)) {
            if (ItemFunctions.removeItem(player, 57, 10000, "KeplonInstance") >= 10000) {
                ItemFunctions.addItem(player, 4401, 1, "KeplonInstance");
            } else {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            }
        } else if (command.startsWith("buyblue")) {
            if (ItemFunctions.removeItem(player, 57, 10000, "KeplonInstance") >= 10000) {
                ItemFunctions.addItem(player, 4402, 1, "KeplonInstance");
            } else {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            }
        } else if (command.startsWith("buyred")) {
            if (ItemFunctions.removeItem(player, 57, 10000, "KeplonInstance") >= 10000) {
                ItemFunctions.addItem(player, 4403, 1, "KeplonInstance");
            } else {
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            }
        } else
            super.onBypassFeedback(player, command);
    }
}