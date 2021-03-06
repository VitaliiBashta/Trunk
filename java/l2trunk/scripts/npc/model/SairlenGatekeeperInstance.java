package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.scripts.bosses.SailrenManager;

public final class SairlenGatekeeperInstance extends NpcInstance {
    private static final int GAZKH = 8784;

    public SairlenGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("request_entrance")) {
            if (player.getLevel() < 75)
                showChatWindow(player, "default/32109-3.htm");
            else if (player.haveItem( GAZKH) ) {
                int check = SailrenManager.canIntoSailrenLair(player);
                if (check == 1 || check == 2)
                    showChatWindow(player, "default/32109-5.htm");
                else if (check == 3)
                    showChatWindow(player, "default/32109-4.htm");
                else if (check == 4)
                    showChatWindow(player, "default/32109-1.htm");
                else if (check == 0) {
                    ItemFunctions.removeItem(player, GAZKH, 1, "SairlenGatekeeperInstance");
                    SailrenManager.setSailrenSpawnTask();
                    SailrenManager.entryToSailrenLair(player);
                }
            } else
                showChatWindow(player, "default/32109-2.htm");
        } else
            super.onBypassFeedback(player, command);
    }
}