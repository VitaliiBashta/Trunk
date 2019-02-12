package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class KegorNpcInstance extends NpcInstance {
    public KegorNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String htmlpath;
        if (getReflection().isDefault())
            htmlpath = "default/32761-default.htm";
        else
            htmlpath = "default/32761.htm";
        return htmlpath;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_stone".equalsIgnoreCase(command)) {
            if (player.haveItem(15469)  || player.haveItem(15470)) {
                player.sendMessage("You can't take more than 1 Frozen Core.");
            } else {
                addItem(player, 15469, 1);
            }
        } else
            super.onBypassFeedback(player, command);
    }
}