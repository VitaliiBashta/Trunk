package l2trunk.scripts.npc.model.birthday;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

public final class AlegriaInstance extends NpcInstance {
    private static final int EXPLORERHAT = 10250;
    private static final int HAT = 13488; // Birthday Hat

    public AlegriaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("exchangeHat".equalsIgnoreCase(command)) {
            if (!player.haveItem(EXPLORERHAT)) {
                showChatWindow(player, "default/32600-nohat.htm");
                return;
            }

            ItemFunctions.removeItem(player, EXPLORERHAT, 1, "AlegriaInstance");
            ItemFunctions.addItem(player, HAT, 1, "AlegriaInstance");

            showChatWindow(player, "default/32600-successful.htm");

            deleteMe();
        } else
            super.onBypassFeedback(player, command);
    }
}
